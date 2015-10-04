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
import java.io.FilenameFilter;
import java.util.Arrays;
import static java.util.Collections.list;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.overlay.FixedPixelCircle;
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

/**
 *
 * @author jan
 */
public class GeoTrip {
    
    private MainFrame mainFrame;
    
    public static GeoPoint ref;

    private List<File> mapFiles;
    private CacheStorage cacheStorage;
    
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

        findMapFiles("D:\\Stahování\\GeoTrip");      
        addMapFiles(mapFiles);
        
        GpxReader gpxReader = new GpxReader();
        ref = gpxReader.readRef();
        
        cacheStorage = new CacheStorage();
        cacheStorage.addCaches(gpxReader.readCaches());
        
        addRef();
        mainFrame.zoomToRef();
        
        addCaches(cacheStorage.getCacheList());
        
        
    }
    
    private void findMapFiles(String folder) {
        File dir = new File(folder);
         
        File[] arr = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".map");
            }
        });  
        
        mapFiles = Arrays.asList(arr);
    }
    
    private void addMapFiles(List<File> mapFiles) {
        Layers layers = mainFrame.mapView.getLayerManager().getLayers();

        BoundingBox result = null;
        for (int i = 0; i < mapFiles.size(); i++) {
            File mapFile = mapFiles.get(i);
            TileRendererLayer tileRendererLayer = createTileRendererLayer(createTileCache(i), mainFrame.mapViewModel.mapViewPosition, true, true, mapFile);
            BoundingBox tmp = tileRendererLayer.getMapDataStore().boundingBox();
            result = result == null ? tmp : result.extend(tmp);
            layers.add(tileRendererLayer);
        }
        
        mainFrame.boundingBox = result;
    }

    private TileCache createTileCache(int index) {
        TileCache firstLevelTileCache = new InMemoryTileCache(128);
        File cacheDirectory = new File(System.getProperty("java.io.tmpdir"), "mapsforge" + index);
        TileCache secondLevelTileCache = new FileSystemTileCache(1024, cacheDirectory, mainFrame.GRAPHIC_FACTORY);
        return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
    }

    private TileRendererLayer createTileRendererLayer(TileCache tileCache, MapViewPosition mapViewPosition, boolean isTransparent, boolean renderLabels, File mapFile) {
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, new MapFile(mapFile), mapViewPosition, isTransparent, renderLabels, mainFrame.GRAPHIC_FACTORY);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        return tileRendererLayer;
    }
    
    private void addRef() {
        Layers layers = mainFrame.mapView.getLayerManager().getLayers();
        
        Paint paint = mainFrame.GRAPHIC_FACTORY.createPaint();
        paint.setColor(org.mapsforge.core.graphics.Color.RED);
        
        FixedPixelCircle circle = new FixedPixelCircle(ref.coordinates, 10, paint, null);
        
        layers.add(circle);
    }
    
    private void addCaches(List<Cache> caches) {
        Layers layers = mainFrame.mapView.getLayerManager().getLayers();
        
        Paint paint = MainFrame.GRAPHIC_FACTORY.createPaint();
        paint.setColor(org.mapsforge.core.graphics.Color.BLUE);
        
        FixedPixelCircle circle;
        
        for (Cache cache : caches) {
            circle = new FixedPixelCircle(cache.coordinates, 10, paint, null);
            layers.add(circle);
        }
    }
    
    public void filter(boolean found, int container, int difficultyLow, int difficultyHigh, int terrainLow, int terrainHigh) {
        Layers layers = mainFrame.mapView.getLayerManager().getLayers();
        layers.clear();
        addMapFiles(mapFiles);
        addRef();
        addCaches(cacheStorage.getCacheList());
    }
}
