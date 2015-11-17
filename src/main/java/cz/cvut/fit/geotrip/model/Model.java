/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.model;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import cz.cvut.fit.geotrip.GeoTrip;
import cz.cvut.fit.geotrip.data.GeoCache;
import cz.cvut.fit.geotrip.data.GeoPoint;
import cz.cvut.fit.geotrip.data.dao.CacheDAO;
import cz.cvut.fit.geotrip.data.dao.DAOFactory;
import cz.cvut.fit.geotrip.data.dao.GpxDAO;
import cz.cvut.fit.geotrip.view.ErrorDialogObserver;
import cz.cvut.fit.geotrip.view.InformationDialogObserver;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.reader.ReadBuffer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

/**
 *
 * @author jan
 */
public class Model {

    private CacheDAO cacheDAO;
    private GpxDAO gpxDAO;
    
    private static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;
    
    private Preferences prefs;

    private String mapName;
    
    private BoundingBox boundingBox;
    private Layers layers;
    private Layer mapLayer = null;
    private Layer routeLayer = null;
    private final List<Layer> cacheLayers;
    private final Map<Layer, GeoCache> layersCache;
    
    private Bitmap iconFound, iconNotFound, iconRef;

    private MapViewPosition mapViewPosition;
    
    private final GeoPoint refPoint;

    
    public Model() {
        loadSettings();

        refPoint = DAOFactory.getDAOFactory().getGpxDAO().getRef();
        cacheDAO = DAOFactory.getDAOFactory().getCacheDAO();
        
        layersCache = new HashMap<>();
        cacheLayers = new LinkedList<>();
    }

    public void load() {
        loadMarkersIcons();
        loadMap();
        loadCaches();
        loadRef();
    }
    
    private void loadSettings() {
        prefs = Preferences.userNodeForPackage(GeoTrip.class);
    }
    
    private void loadMarkersIcons() {
        try (InputStream iconFoundIS = getClass().getClassLoader().getResourceAsStream("markers/found.png");
                InputStream iconNotFoundIS = getClass().getClassLoader().getResourceAsStream("markers/notfound.png");
                InputStream iconRefIS = getClass().getClassLoader().getResourceAsStream("markers/ref.png");) {
            iconFound = GRAPHIC_FACTORY.createResourceBitmap(iconFoundIS, 0);
            iconNotFound = GRAPHIC_FACTORY.createResourceBitmap(iconNotFoundIS, 0);
            iconRef = GRAPHIC_FACTORY.createResourceBitmap(iconRefIS, 0);
        } catch (IOException | IllegalArgumentException ex) {
            ErrorDialogObserver errorDialogObserver = new ErrorDialogObserver();
            errorDialogObserver.update("Chybejici ikona", "");
        } 
    }
    
    public void setLayers(Layers layers) {
        this.layers = layers;
    }
    
    public Layers getLayers() {
        return layers;
    }
    
    public void setMapViewPosition(MapViewPosition mapViewPosition) {
        this.mapViewPosition = mapViewPosition;
    }
    
    public void changeMap(String mapName) {
        prefs.put("map", mapName);
        loadMap();
    }
    
    public void loadMap() {
        ReadBuffer.setMaximumBufferSize(6500000);

        mapName = prefs.get("map", null);

        if (mapName == null) {
            InformationDialogObserver informationDialogObserver = new InformationDialogObserver();
            informationDialogObserver.update("Chybejici mapa", "Pridejte mapu do slozky data/maps a vyberte ji v nastaveni.");
            return;
        }

        File mapFile = new File("data/maps/" + mapName + ".map");
        File osmFile = new File("data/maps/" + mapName + ".osm.pbf");
        if (!mapFile.exists() || !osmFile.exists()) {
            ErrorDialogObserver errorDialogObserver = new ErrorDialogObserver();
            errorDialogObserver.update("Chybejici mapa", "Soubor s mapou nenalezen.");
            return;
        }
        
        addMapLayer(mapFile);
    }

    public void addMapLayer(File mapFile) {
        if (mapLayer != null)
            layers.remove(mapLayer);
        
        TileRendererLayer tileRendererLayer = createTileRendererLayer(createTileCache(0), mapViewPosition, true, true, mapFile);
        mapLayer = tileRendererLayer;
        layers.add(0, mapLayer);
    }
    
    private TileCache createTileCache(int index) {
        TileCache firstLevelTileCache = new InMemoryTileCache(128);
        File cacheDirectory = new File(System.getProperty("java.io.tmpdir"), "mapsforge" + index);
        TileCache secondLevelTileCache = new FileSystemTileCache(1024, cacheDirectory, GRAPHIC_FACTORY);
        return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
    }

    private TileRendererLayer createTileRendererLayer(TileCache tileCache, MapViewPosition mapViewPosition, boolean isTransparent, boolean renderLabels, File mapFile) {
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, new MapFile(mapFile), mapViewPosition, isTransparent, renderLabels, GRAPHIC_FACTORY);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        return tileRendererLayer;
    }
    
    public void loadRef() {
        addRefMarker(refPoint.getCoordinates());
    }
    
    public void addRefMarker(LatLong coordinates) {
        Marker marker = new Marker(coordinates, iconRef, 0, -iconRef.getHeight()/2);
        layers.add(marker);
    }
        
    public void loadCaches() {
        loadCaches(cacheDAO.getAll());
    }
    
    public void loadCaches(Collection<GeoCache> caches) {
        layersCache.clear();

        Marker marker;
        for (GeoCache cache : caches) {
            marker = addCacheMarker(cache.getCoordinates(), cache.isFound());
            layersCache.put(marker, cache);
        }
    }

    public Marker addCacheMarker(LatLong coordinates, boolean found) {
        Marker marker = new Marker(coordinates, found ? iconFound : iconNotFound, 0, -iconRef.getHeight()/2) {
            @Override
            public boolean onTap(LatLong cacheLatLong, Point cachePosition, Point clickPosition) {
                double dX = cachePosition.x - clickPosition.x;
                double dY = cachePosition.y - clickPosition.y;
                return dY < getBitmap().getHeight() && dY > 3 && Math.abs(dX) <= getBitmap().getWidth()/2;
            }
        };
        layers.add(marker);
        cacheLayers.add(marker);
        return marker;
    }
    
    public void removeCacheMarkers() {
        for (Layer layer : cacheLayers)
            layers.remove(layer);
    }
    
    public void addRoute(PointList pl) {
        Paint paint = GRAPHIC_FACTORY.createPaint();
        paint.setColor(org.mapsforge.core.graphics.Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Style.STROKE);
    
        Polyline polyline = new Polyline(paint, GRAPHIC_FACTORY);
        
        for (int i = 0; i < pl.size(); i++)
            polyline.getLatLongs().add(new LatLong(pl.getLat(i), pl.getLon(i)));
        
        layers.add(1, polyline);
        routeLayer = polyline;
    }

    public void removeRoute() {
        if (routeLayer != null)
            layers.remove(routeLayer);
        routeLayer = null;
    }
    
    public void filter(int distance, String vehicle, final boolean found, final int container,
            final int difficultyLow, final int difficultyHigh, final int terrainLow, final int terrainHigh) {
        removeCacheMarkers();

        Predicate<GeoCache> predicateFound = new Predicate<GeoCache>() {
            @Override
            public boolean apply(GeoCache gc) {
                return found || !gc.isFound();
            }
        };
        
        Predicate<GeoCache> predicateContainer = new Predicate<GeoCache>() {
            @Override
            public boolean apply(GeoCache gc) {
                return  (container & gc.getContainer().getValue()) != 0;
            }
        };
        
        Predicate<GeoCache> predicateDifficulty = new Predicate<GeoCache>() {
            @Override
            public boolean apply(GeoCache gc) {
                return gc.getDifficulty() >= difficultyLow && gc.getDifficulty() <= difficultyHigh;
            }
        };
                
        Predicate<GeoCache> predicateTerrain = new Predicate<GeoCache>() {
            @Override
            public boolean apply(GeoCache gc) {
                return gc.getTerrain() >= terrainLow && gc.getTerrain() <= terrainHigh;
            }
        };
        
        Predicate predicateAnd = Predicates.and(predicateFound, predicateContainer, predicateDifficulty, predicateTerrain);
        
        List<GeoCache> filteredList = new LinkedList<>(cacheDAO.getFiltered(predicateAnd));
        
        loadCaches(filteredList);

        GraphHopper gh = new GraphHopper().setEncodingManager(new EncodingManager(vehicle)).forDesktop();
        gh.load("data/gh/" + mapName + "/" + vehicle);

        PointList pl = new PointList();
        GHRequest req;
        GHResponse resp;

        boundingBox = new BoundingBox(refPoint.getLat(), refPoint.getLon(), refPoint.getLat(), refPoint.getLon());

        req = new GHRequest(refPoint.getLat(), refPoint.getLon(), filteredList.get(0).getLat(), filteredList.get(0).getLon());
        req.setAlgorithm(AlgorithmOptions.ASTAR_BI);
        req.getHints().put("instructions", false);
        req.setVehicle(vehicle);
        resp = gh.route(req);
        pl.add(refPoint.getLat(), refPoint.getLon());
        pl.add(resp.getPoints());

        int i;
        for (i = 1; i < filteredList.size(); i++) {
            req = new GHRequest(filteredList.get(i - 1).getLat(), filteredList.get(i - 1).getLon(), filteredList.get(i).getLat(), filteredList.get(i).getLon());
            req.setAlgorithm(AlgorithmOptions.ASTAR_BI);
            req.getHints().put("instructions", false);
            req.setVehicle(vehicle);
            resp = gh.route(req);
            pl.add(filteredList.get(i - 1).getLat(), filteredList.get(i - 1).getLon());
            pl.add(resp.getPoints());
            boundingBox = boundingBox.extend(new BoundingBox(filteredList.get(i).getLat(), filteredList.get(i).getLon(), filteredList.get(i).getLat(), filteredList.get(i).getLon()));
        }

        req = new GHRequest(filteredList.get(i - 1).getLat(), filteredList.get(i - 1).getLon(), refPoint.getLat(), refPoint.getLon());
        req.setAlgorithm(AlgorithmOptions.ASTAR_BI);
        req.getHints().put("instructions", false);
        req.setVehicle(vehicle);
        resp = gh.route(req);
        pl.add(filteredList.get(i - 1).getLat(), filteredList.get(i - 1).getLon());
        pl.add(resp.getPoints());
        pl.add(refPoint.getLat(), refPoint.getLon());
        boundingBox = boundingBox.extend(new BoundingBox(filteredList.get(i - 1).getLat(), filteredList.get(i - 1).getLon(), filteredList.get(i - 1).getLat(), filteredList.get(i - 1).getLon()));

        removeRoute();
        addRoute(pl);
    }

    public GeoCache getCacheByLayer(Layer layer) {
        return layersCache.get(layer);
    }
    
    public GeoPoint getRefPoint() {
        return refPoint;
    }
    
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public List<String> getMapList() {
        File dir = new File("data/maps");
        
        List<String> res = new LinkedList<>();
        List<String> tmp = new LinkedList<>();
        
        File[] mapArray = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".map");
            }
        }); 
        
        File[] osmArray = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".osm.pbf");
            }
        });
        
        for (File f : mapArray) 
            tmp.add(f.getName().split(Pattern.quote("."))[0]);
        
        for (File f : osmArray) {
            String name = f.getName().split(Pattern.quote("."))[0];
            if (tmp.contains(name)) 
                res.add(name);
        }
        
        return res;
    }
}
