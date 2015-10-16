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
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import cz.cvut.fit.geotrip.geopoint.GeoCache;
import cz.cvut.fit.geotrip.geopoint.CacheStorage;
import cz.cvut.fit.geotrip.geopoint.GeoPoint;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.reader.ReadBuffer;

/**
 *
 * @author jan
 */
public class GeoTrip {
    
    private MainFrame mainFrame;
    
    private static GeoPoint refPoint;

    private CacheStorage cacheStorage;
    private Map<Layer, GeoCache> layersCache;
    
    private String mapName;
    
    private Preferences prefs;
        
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
        
        loadSettings();
        
        mainFrame = new MainFrame(this);
        mainFrame.setVisible(true);

        ReadBuffer.setMaximumBufferSize(6500000);

        loadMap();
        
        GpxReader gpxReader = new GpxReader();
        refPoint = gpxReader.readRef();
        
        cacheStorage = new CacheStorage();
        cacheStorage.addCaches(gpxReader.readCaches());
        
        mainFrame.addRefMarker(refPoint.getCoordinates());
        mainFrame.moveTo(refPoint.getCoordinates());
        
        layersCache = new HashMap<>();
        
        addCaches(cacheStorage.getCacheList());
    }
    
    private void loadSettings() {
        prefs = Preferences.userNodeForPackage(GeoTrip.class);
        
        changeLanguage(prefs.get("language", "en-US"));
    }
    
    public void loadMap() {
        changeLanguage(prefs.get("language", "en-US"));
        mapName = prefs.get("map", null);
        
        if (mapName == null) {
            JOptionPane.showMessageDialog(mainFrame, "Pridejte mapu do slozky data/maps a vyberte ji v nastaveni.", "Chybejici mapa", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
  
        File mapFile = new File("data/maps/" + mapName + ".map");
        File osmFile = new File("data/maps/" + mapName + ".osm.pbf");
        if (!mapFile.exists() || !osmFile.exists()) {
            JOptionPane.showMessageDialog(mainFrame, "Soubor s mapou nenalezen.", "Chybejici mapa", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        mainFrame.addMap(mapFile);
    }
    
    private void changeLanguage(String language) {
        Locale.setDefault(Locale.forLanguageTag(language));
    }
    
    private void addCaches(List<GeoCache> caches) {
        layersCache.clear();
        
        Marker marker;
        for (GeoCache cache : caches) {
            marker = mainFrame.addCacheMarker(cache.getCoordinates(), cache.isFound());
            layersCache.put(marker, cache);
        }
    }

    public void filter(int distance, String vehicle, boolean found, int container, int difficultyLow, int difficultyHigh, int terrainLow, int terrainHigh) {
        mainFrame.removeCacheMarkers();
        
        List<GeoCache> filteredList = cacheStorage.getFilteredList(found, container, difficultyLow, difficultyHigh, terrainLow, terrainHigh);
        addCaches(filteredList);
        
        GraphHopper gh = new GraphHopper().setEncodingManager(new EncodingManager(vehicle)).forDesktop();
        gh.load("data/gh/" + mapName + "/" + vehicle);
       
        PointList pl = new PointList();
        GHRequest req;
        GHResponse resp;
        
        BoundingBox bb = new BoundingBox(refPoint.getLat(), refPoint.getLon(), refPoint.getLat(), refPoint.getLon());
        
        req = new GHRequest(refPoint.getLat(), refPoint.getLon(), filteredList.get(0).getLat(), filteredList.get(0).getLon());
        req.setAlgorithm(AlgorithmOptions.ASTAR_BI);
        req.getHints().put("instructions", false);
        req.setVehicle(vehicle);
        resp = gh.route(req);
        pl.add(refPoint.getLat(), refPoint.getLon());
        pl.add(resp.getPoints());
        
        int i;
        for (i = 1; i < filteredList.size(); i++) {
            req = new GHRequest(filteredList.get(i-1).getLat(), filteredList.get(i-1).getLon(), filteredList.get(i).getLat(), filteredList.get(i).getLon());
            req.setAlgorithm(AlgorithmOptions.ASTAR_BI);
            req.getHints().put("instructions", false);
            req.setVehicle(vehicle);
            resp = gh.route(req);
            pl.add(filteredList.get(i-1).getLat(), filteredList.get(i-1).getLon());
            pl.add(resp.getPoints());
            bb = bb.extend(new BoundingBox(filteredList.get(i).getLat(), filteredList.get(i).getLon(), filteredList.get(i).getLat(), filteredList.get(i).getLon()));
        }
        
        req = new GHRequest(filteredList.get(i-1).getLat(), filteredList.get(i-1).getLon(), refPoint.getLat(), refPoint.getLon());
        req.setAlgorithm(AlgorithmOptions.ASTAR_BI);
        req.getHints().put("instructions", false);
        req.setVehicle(vehicle);
        resp = gh.route(req);
        pl.add(filteredList.get(i-1).getLat(), filteredList.get(i-1).getLon());
        pl.add(resp.getPoints());
        pl.add(refPoint.getLat(), refPoint.getLon());
        bb = bb.extend(new BoundingBox(filteredList.get(i-1).getLat(), filteredList.get(i-1).getLon(), filteredList.get(i-1).getLat(), filteredList.get(i-1).getLon()));
        
        mainFrame.removeRoute();
        mainFrame.addRoute(pl);
        mainFrame.zoomTo(bb);
    }
    
    public void showCacheInfo(Layer layer) {
        GeoCache cache = layersCache.get(layer);
        mainFrame.showCacheInfo(cache.getName(), cache.formatCoordinates(), cache.getContainer(), cache.getDifficulty(), cache.getTerrain(), cache.getId(), cache.getLink());
    }
    
    public static GeoPoint getRefPoint() {
        return refPoint;
    }
}
