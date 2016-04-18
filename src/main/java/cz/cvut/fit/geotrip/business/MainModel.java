package cz.cvut.fit.geotrip.business;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import cz.cvut.fit.geotrip.GeoTrip;
import cz.cvut.fit.geotrip.business.router.Router;
import cz.cvut.fit.geotrip.business.router.impl.RouterGH;
import cz.cvut.fit.geotrip.business.tripplanner.TripPlanner;
import cz.cvut.fit.geotrip.data.GpxExport;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import cz.cvut.fit.geotrip.data.dao.CacheDAO;
import cz.cvut.fit.geotrip.data.dao.DAOFactory;
import cz.cvut.fit.geotrip.data.dao.GpxDAO;
import cz.cvut.fit.geotrip.data.entities.GeoPoint;
import cz.cvut.fit.geotrip.presentation.view.CenterMapObserver;
import cz.cvut.fit.geotrip.presentation.view.ErrorDialog;
import cz.cvut.fit.geotrip.presentation.view.InformationDialog;
import cz.cvut.fit.geotrip.presentation.view.InstalledMapsObserver;
import cz.cvut.fit.geotrip.presentation.view.MapImportDialogObserver;
import cz.cvut.fit.geotrip.presentation.view.PlanningDialogObserver;
import cz.cvut.fit.geotrip.utils.Texts;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Cap;
import org.mapsforge.core.graphics.Color;
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

public class MainModel {

    private static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;

    private final String MAPS_DIRECTORY = "maps/";
    private final String MAP_EXTENSION = ".map";
    private final String OSM_EXTENSION = ".osm.pbf";

    private final CacheDAO cacheDAO;
    private GpxDAO gpxDAO;

    private Preferences prefs;
    private String mapName;

    private BoundingBox boundingBox;
    private Layers layers;
    private Layer mapLayer = null;
    private Layer routeLayer = null;
    private final List<Layer> cacheLayers;
    private final List<String> installedMaps;
    private final Map<Layer, GeoCache> layersCache;

    private Bitmap iconFound, iconNotFound, iconRef;
    private MapViewPosition mapViewPosition;

    private final GeoPlace refPoint;

    private TripPlanner tripPlanner;

    List<GeoPlace> trip;

    private InstalledMapsObserver installedMapsObserver;
    private CenterMapObserver centerMapObserver;
    private MapImportDialogObserver mapImportDialogObserver;
    private PlanningDialogObserver planningDialogObserver;

    public MainModel() {
        refPoint = DAOFactory.getDAOFactory().getGpxDAO().getRef();
        cacheDAO = DAOFactory.getDAOFactory().getCacheDAO();

        layersCache = new HashMap<>();
        cacheLayers = new LinkedList<>();
        installedMaps = new LinkedList<>();

        loadSettings();
        loadMarkersIcons();
    }

    /**
     * Inits map, prints caches
     */
    public void init() {
        findInstalledMaps();
        loadMap();
        MainModel.this.printCaches();
        loadRef();
    }

    public void registerInstalledMapsObserver(InstalledMapsObserver installedMapsObserver) {
        this.installedMapsObserver = installedMapsObserver;
    }

    public void registerCenterMapObserver(CenterMapObserver centerMapObserver) {
        this.centerMapObserver = centerMapObserver;
    }

    public void registerMapImportDialogObserver(MapImportDialogObserver mapImportDialogObserver) {
        this.mapImportDialogObserver = mapImportDialogObserver;
    }

    public void registerPlanningDialogObserver(PlanningDialogObserver planningDialogObserver) {
        this.planningDialogObserver = planningDialogObserver;
    }

    public void setMapViewPosition(MapViewPosition mapViewPosition) {
        this.mapViewPosition = mapViewPosition;
    }

    /**
     * Changes map.
     * @param mapName name of map 
     */
    public void changeMap(String mapName) {
        prefs.put("map", mapName);
        loadMap();
    }

    /**
     * Returns selected map.
     * 
     * @return selected map
     */
    public String getSelectedMap() {
        return prefs.get("map", null);
    }

    /**
     * Imports map.
     * 
     * @param mapFile .map file
     * @param osmFile .osm file
     */
    public void importMap(final File mapFile, final File osmFile) {
        new Thread() {
            @Override
            public void run() {
                String mapName = osmFile.getName().replaceFirst(OSM_EXTENSION, "");

                File mapFileDest = new File(GeoTrip.DATA_DIRECTORY + MAPS_DIRECTORY + mapName + MAP_EXTENSION);
                File osmFileDest = new File(GeoTrip.DATA_DIRECTORY + MAPS_DIRECTORY + mapName + OSM_EXTENSION);

                try {
                    Files.copy(mapFile.toPath(), mapFileDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(osmFile.toPath(), osmFileDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ErrorDialog.getInstance().show(Texts.getInstance().getLocalizedText("mapCopyErrorTitle"), Texts.getInstance().getLocalizedText("mapCopyErrorMessage"));
                }

                Router router = new RouterGH();
                router.importMap(mapName);
                mapImportDialogObserver.update();
                findInstalledMaps();
            }
        }.start();
    }

    public void setLayers(Layers layers) {
        this.layers = layers;
    }

    public Layers getLayers() {
        return layers;
    }

    /**
     * Returns cache by its layer.
     * 
     * @param layer
     * @return cache
     */
    public GeoCache getCacheByLayer(Layer layer) {
        return layersCache.get(layer);
    }

    /**
     * Returns ref point.
     * 
     * @return ref point
     */
    public GeoPlace getRefPoint() {
        return refPoint;
    }

    /**
     * Returns bounding box.
     * 
     * @return bounding box
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * Starts trip planning
     * 
     * @param distance
     * @param vehicle
     * @param tripType
     * @param found
     * @param container
     * @param difficultyLow
     * @param difficultyHigh
     * @param terrainLow
     * @param terrainHigh
     * @param containerPriority
     * @param difficultyPriority
     * @param terrainPriority
     * @return true if successful
     */
    public boolean planTrip(int distance, RoutingTypes vehicle, TripType tripType, boolean found, int container, int difficultyLow, int difficultyHigh, int terrainLow,
            int terrainHigh, int containerPriority, int difficultyPriority, int terrainPriority) {
        removeCacheMarkers();
        removeRoute();

        List<GeoCache> filteredCaches = getFilteredCaches(distance, found, container, difficultyLow, difficultyHigh, terrainLow, terrainHigh);

        tripPlanner = new TripPlanner(mapName, vehicle, tripType, refPoint, filteredCaches, distance, containerPriority, difficultyPriority, terrainPriority, planningDialogObserver);

        new Thread(tripPlanner).start();

        planningDialogObserver.show();

        List<GeoPoint> route = tripPlanner.getTripRoutePoints();
        trip = tripPlanner.getTripPoints();

        if (route == null) {
            InformationDialog.getInstance().show(Texts.getInstance().getLocalizedText("noTripFoundTitle"), Texts.getInstance().getLocalizedText("noTripFoundMessage"));
            return false;
        }

        addRoute(route);
        countBoundingBox(route);
        printCaches(filteredCaches);

        return true;
    }

    /**
     * Returns trip length.
     * 
     * @return trip length 
     */
    public String getTripLength() {
        return String.format("%.1f", tripPlanner.getTripLength() / 1000);
    }

    /**
     * Returns trip total time.
     * 
     * @return trip total time
     */
    public String getTripTime() {
        long time = tripPlanner.getTripTime() / (60 * 1000);
        long hours = time / 60;
        long minutes = time - hours * 60;
        return String.format("%d:%02d", hours, minutes);
    }

    /**
     * Returns count of caches.
     * 
     * @return count of caches
     */
    public String getTripCaches() {
        return String.format("%d", tripPlanner.getTripCaches());
    }

    /**
     * Exports to gpx.
     * 
     * @param file output file
     */
    public void exportToGpx(File file) {
        GpxExport gpxExport = new GpxExport(file);
        gpxExport.export(trip);
    }

    private void findInstalledMaps() {
        installedMaps.clear();

        File dir = new File(GeoTrip.DATA_DIRECTORY + MAPS_DIRECTORY);

        List<String> tmp = new LinkedList<>();

        File[] mapArray = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(MAP_EXTENSION);
            }
        });

        File[] osmArray = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(OSM_EXTENSION);
            }
        });

        for (File f : mapArray) {
            tmp.add(f.getName().split(Pattern.quote("."))[0]);
        }

        for (File f : osmArray) {
            String name = f.getName().split(Pattern.quote("."))[0];
            if (tmp.contains(name)) {
                installedMaps.add(name);
            }
        }

        installedMapsObserver.update(installedMaps);
    }

    private void loadSettings() {
        prefs = Preferences.userNodeForPackage(GeoTrip.class);
    }

    private void loadMap() {
        ReadBuffer.setMaximumBufferSize(6500000);

        mapName = prefs.get("map", null);

        if (mapName == null) {
            InformationDialog.getInstance().show(Texts.getInstance().getLocalizedText("noMapTitle"), Texts.getInstance().getLocalizedText("noMapMessage"));
            return;
        }

        File mapFile = new File(GeoTrip.DATA_DIRECTORY + MAPS_DIRECTORY + mapName + MAP_EXTENSION);
        File osmFile = new File(GeoTrip.DATA_DIRECTORY + MAPS_DIRECTORY + mapName + OSM_EXTENSION);
        if (!mapFile.exists() || !osmFile.exists()) {
            ErrorDialog.getInstance().show(Texts.getInstance().getLocalizedText("missingMapTitle"), Texts.getInstance().getLocalizedText("missingMapMessage"));
            return;
        }

        addMapLayer(mapFile);
    }

    private void loadMarkersIcons() {
        try (InputStream iconFoundIS = getClass().getClassLoader().getResourceAsStream("markers/found.png");
                InputStream iconNotFoundIS = getClass().getClassLoader().getResourceAsStream("markers/notfound.png");
                InputStream iconRefIS = getClass().getClassLoader().getResourceAsStream("markers/ref.png");) {
            iconFound = GRAPHIC_FACTORY.createResourceBitmap(iconFoundIS, 0);
            iconNotFound = GRAPHIC_FACTORY.createResourceBitmap(iconNotFoundIS, 0);
            iconRef = GRAPHIC_FACTORY.createResourceBitmap(iconRefIS, 0);
        } catch (IOException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void addMapLayer(File mapFile) {
        if (mapLayer != null) {
            layers.remove(mapLayer);
        }

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

    private void loadRef() {
        addRefMarker(refPoint.getCoordinates());
        centerMapObserver.update(refPoint.getCoordinates());
    }

    private void addRefMarker(GeoPoint coordinates) {
        Marker marker = new Marker(new LatLong(coordinates.getLat(), coordinates.getLon()), iconRef, 0, -iconRef.getHeight() / 2);
        layers.add(marker);
    }

    private void printCaches() {
        printCaches(cacheDAO.getAll());
    }

    private void printCaches(Collection<GeoCache> caches) {
        layersCache.clear();

        Marker marker;
        for (GeoCache cache : caches) {
            marker = createCacheMarker(new LatLong(cache.getCoordinates().getLat(), cache.getCoordinates().getLon()), cache.isFound());
            layersCache.put(marker, cache);
        }
    }

    private Marker createCacheMarker(LatLong coordinates, boolean found) {
        Marker marker = new Marker(coordinates, found ? iconFound : iconNotFound, 0, -iconRef.getHeight() / 2) {
            @Override
            public boolean onTap(LatLong cacheLatLong, Point cachePosition, Point clickPosition) {
                double dX = cachePosition.x - clickPosition.x;
                double dY = cachePosition.y - clickPosition.y;
                return dY < getBitmap().getHeight() && dY > 3 && Math.abs(dX) <= getBitmap().getWidth() / 2;
            }
        };
        layers.add(marker);
        cacheLayers.add(marker);
        return marker;
    }

    private void removeCacheMarkers() {
        for (Layer layer : cacheLayers) {
            layers.remove(layer);
        }
    }

    private void addRoute(List<GeoPoint> route) {
        Paint paint = GRAPHIC_FACTORY.createPaint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        float[] dash = {10, 15};
        paint.setDashPathEffect(dash);
        paint.setStrokeCap(Cap.SQUARE);
        paint.setStyle(Style.STROKE);

        Polyline polyline = new Polyline(paint, GRAPHIC_FACTORY);

        for (GeoPoint gp : route) {
            polyline.getLatLongs().add(new LatLong(gp.getLat(), gp.getLon()));
        }

        layers.add(1, polyline);
        routeLayer = polyline;
    }

    private void removeRoute() {
        if (routeLayer != null) {
            layers.remove(routeLayer);
        }
        routeLayer = null;
    }

    private void countBoundingBox(List<GeoPoint> route) {
        boundingBox = new BoundingBox(refPoint.getLat(), refPoint.getLon(), refPoint.getLat(), refPoint.getLon());
        for (GeoPoint p : route) {
            boundingBox = boundingBox.extend(new BoundingBox(p.getLat(), p.getLon(), p.getLat(), p.getLon()));
        }
    }

    private List<GeoCache> getFilteredCaches(final int distance, final boolean found, final int container,
            final int difficultyLow, final int difficultyHigh, final int terrainLow, final int terrainHigh) {
        Predicate<GeoCache> predicateDistance = new Predicate<GeoCache>() {
            @Override
            public boolean apply(GeoCache gc) {
                return gc.getCircleDistance(refPoint) < (distance / 2);
            }
        };

        Predicate<GeoCache> predicateFound = new Predicate<GeoCache>() {
            @Override
            public boolean apply(GeoCache gc) {
                return found || !gc.isFound();
            }
        };

        Predicate<GeoCache> predicateContainer = new Predicate<GeoCache>() {
            @Override
            public boolean apply(GeoCache gc) {
                return (container & gc.getContainer().getValue()) != 0;
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

        Predicate predicateAnd = Predicates.and(predicateDistance, predicateFound, predicateContainer, predicateDifficulty, predicateTerrain);

        return new LinkedList<>(cacheDAO.getFiltered(predicateAnd));
    }

}
