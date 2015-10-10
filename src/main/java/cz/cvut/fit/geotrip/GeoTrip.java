/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.AlgorithmOptions;
import cz.cvut.fit.geotrip.geopoint.Cache;
import cz.cvut.fit.geotrip.geopoint.CacheStorage;
import cz.cvut.fit.geotrip.geopoint.GeoPoint;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import static java.util.Collections.list;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tile;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.awt.AwtBitmap;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.labels.LabelLayer;
import org.mapsforge.map.layer.overlay.FixedPixelCircle;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.reader.ReadBuffer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.swing.controller.MapViewComponentListener;
import org.mapsforge.map.swing.controller.MouseEventListener;
import org.mapsforge.map.swing.view.MapView;
import org.mapsforge.map.util.MapViewProjection;

/**
 *
 * @author jan
 */
public class GeoTrip {
    
    private MainFrame mainFrame;
    
    public static GeoPoint ref;

    private List<Layer> mapLayers;
    private CacheStorage cacheStorage;
    private Layers layers;
    private Bitmap iconFound, iconNotFound, iconRef;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GeoTrip geoTrip = new GeoTrip();
    }

    private GeoTrip() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) { }
        
        mainFrame = new MainFrame(this);
        mainFrame.setVisible(true);

        ReadBuffer.setMaximumBufferSize(6500000);

        layers = mainFrame.mapView.getLayerManager().getLayers();
        
        addMapFiles(findMapFiles("D:\\Stahování\\GeoTrip"));
        
        
        GpxReader gpxReader = new GpxReader();
        ref = gpxReader.readRef();
        
        cacheStorage = new CacheStorage();
        cacheStorage.addCaches(gpxReader.readCaches());
        
        loadIcons();
        
        addRef();
        mainFrame.zoomToRef();
        
        addCaches(cacheStorage.getCacheList());
        
        
    }
    
    private List<File> findMapFiles(String folder) {
        File dir = new File(folder);
         
        File[] arr = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".map");
            }
        });  
        
        return Arrays.asList(arr);
    }
    
    private void addMapFiles(List<File> mapFiles) {
        mapLayers = new LinkedList<>();
        
        BoundingBox result = null;
        for (int i = 0; i < mapFiles.size(); i++) {
            File mapFile = mapFiles.get(i);
            TileRendererLayer tileRendererLayer = createTileRendererLayer(createTileCache(i), mainFrame.mapViewModel.mapViewPosition, true, true, mapFile);
            BoundingBox tmp = tileRendererLayer.getMapDataStore().boundingBox();
            result = result == null ? tmp : result.extend(tmp);
            mapLayers.add(tileRendererLayer);
        }
        
        layers.addAll(mapLayers);
    }

    private TileCache createTileCache(int index) {
        TileCache firstLevelTileCache = new InMemoryTileCache(128);
        File cacheDirectory = new File(System.getProperty("java.io.tmpdir"), "mapsforge" + index);
        TileCache secondLevelTileCache = new FileSystemTileCache(1024, cacheDirectory, MainFrame.GRAPHIC_FACTORY);
        return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
    }

    private TileRendererLayer createTileRendererLayer(TileCache tileCache, MapViewPosition mapViewPosition, boolean isTransparent, boolean renderLabels, File mapFile) {
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, new MapFile(mapFile), mapViewPosition, isTransparent, renderLabels, mainFrame.GRAPHIC_FACTORY);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        return tileRendererLayer;
    }
    
    private void addRef() {
        Marker marker = new Marker(ref.coordinates, iconRef, 0, -iconRef.getHeight()/2);
        layers.add(marker);
    }
    
    private void addCaches(List<Cache> caches) {
        cacheStorage.clearCacheLayers();
        
        Marker marker;
        int markerVerticalOffset = -iconFound.getHeight()/2;
               
        for (Cache cache : caches) {
            marker = new Marker(cache.coordinates, cache.found ? iconFound : iconNotFound, 0, markerVerticalOffset) {
                @Override
                public boolean onTap(LatLong cacheLatLong, Point cachePosition, Point clickPosition) {
                    double dX = cachePosition.x - clickPosition.x;
                    double dY = cachePosition.y - clickPosition.y;
                    if (dY < getBitmap().getHeight() && dY > 3 && Math.abs(dX) <= getBitmap().getWidth()/2)
                        return true;
                    return false;
                }
            };
            layers.add(marker);
            cacheStorage.addCacheLayer(cache, marker);
        }
    }

    private void loadIcons() {
        try (FileInputStream iconFoundIS = new FileInputStream("D:\\Stahování\\GeoTrip\\cache_found.png");
                InputStream iconNotFoundIS = new FileInputStream("D:\\Stahování\\GeoTrip\\cache_notfound.png");
                InputStream iconRefIS = new FileInputStream("D:\\Stahování\\GeoTrip\\ref.png");) {
            iconFound = MainFrame.GRAPHIC_FACTORY.createResourceBitmap(iconFoundIS, 0);
            iconNotFound = MainFrame.GRAPHIC_FACTORY.createResourceBitmap(iconNotFoundIS, 0);
            iconRef = MainFrame.GRAPHIC_FACTORY.createResourceBitmap(iconRefIS, 0);
        } catch (IOException ex) {
            Logger.getLogger(GeoTrip.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void filter(boolean found, int container, int difficultyLow, int difficultyHigh, int terrainLow, int terrainHigh) {
        layers.clear();
        layers.addAll(mapLayers);
        addRef();
        addCaches(cacheStorage.getFilteredList(found, container, difficultyLow, difficultyHigh, terrainLow, terrainHigh));
    }
    
    public void showInfo(Layer layer) {
        Cache cache = cacheStorage.getCacheByLayer(layer);
        mainFrame.showInfo(cache.name, cache.formatCoordinates(), cache.container, cache.difficulty, cache.terrain, cache.id);
    }
    
    public Layers getLayers() {
        return layers;
    }
}
