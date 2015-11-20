/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.controller;

import cz.cvut.fit.geotrip.data.CacheContainer;
import cz.cvut.fit.geotrip.data.GeoPoint;
import cz.cvut.fit.geotrip.model.MainModel;
import cz.cvut.fit.geotrip.view.MainFrame;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.swing.view.MapView;
import org.mapsforge.map.util.MapViewProjection;

/**
 *
 * @author jan
 */
public class MainController {

    private final MainModel model;
    private final MainFrame view;
    
    public MainController(MainModel model, MainFrame view) {
        this.model = model;
        this.view = view;
    }

    public MainModel getModel() {
        return model;
    }
    
    public MainFrame getView() {
        return view;
    }
    
    public void setLayers(Layers layers) {
        model.setLayers(layers);
    }
    
    public Layers getLayers() {
        return model.getLayers();
    }
    
    public void getSelectedLayer(MapView mapView, Layers layers, int x, int y) {
        view.hideCacheInfo();
        
        MapViewProjection mapViewProjection = new MapViewProjection(mapView);
                
        for (int i = layers.size() - 1; i >= 0; --i) {
            Layer layer = layers.get(i);
            Point layerPosition = mapViewProjection.toPixels(layer.getPosition());
            Point clickPosition = new Point(x, y);
 
            if (layer.onTap(layer.getPosition(), layerPosition, clickPosition)) {
                view.showCacheInfo(model.getCacheByLayer(layer));
                break;
            }
        }
    }
    
    public void setMapViewPosition(MapViewPosition mapViewPosition) {
        model.setMapViewPosition(mapViewPosition);
    }

    public void setZoom(int zoom) {
        if (view.getZoomLevel() != zoom)
            view.setZoomLevel(zoom);
    } 
    
    public void changeZoom(int rotation) {
        setZoom(view.getZoomLevel() - rotation);
    }
    
    public void zoomIn() {
        setZoom(view.getZoomLevel() + 1);
    }
    
    public void zoomOut() {
        setZoom(view.getZoomLevel() - 1);
    }
    
    public GeoPoint getRef() {
        return model.getRefPoint();
    }
    
    public void planTrip(String distanceStr, String vehicleStr, boolean found, boolean containerMicro, boolean containerSmall, boolean containerRegular,
            boolean containerLarge, boolean containerOther, int difficultyLow, int difficultyHigh, int terrainLow, int terrainHigh) {
        
        int distance = Integer.parseInt(distanceStr);
        
        String vehicle = "";
        switch(vehicleStr) {
            case "chůze":
                vehicle = "foot";
                break;
            case "kolo":
                vehicle = "bike";
                break;
            case "auto":
                vehicle = "car";
                break;
        }
                
        int container = 0;

        if (containerMicro)
            container |= CacheContainer.MICRO.getValue();
        if (containerSmall)
            container |= CacheContainer.SMALL.getValue();
        if (containerRegular)
            container |= CacheContainer.REGULAR.getValue();
        if (containerLarge)
            container |= CacheContainer.LARGE.getValue();
        if (containerOther)
            container |= CacheContainer.OTHER.getValue();
        
        view.hideCacheInfo();
        model.filter(distance, vehicle, found, container, difficultyLow, difficultyHigh, terrainLow, terrainHigh);
        view.zoomTo(model.getBoundingBox());
    }
    
    public void addMapsToMenu() {
        for (String s : model.getMapList())
            view.addMapItem(s);
    }
    
    public void openUrl(String link) {
        try {
            URL url = new URL(link);
            URI uri = url.toURI();
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
                desktop.browse(uri);
            else
                JOptionPane.showMessageDialog(null, "Odkaz se nepodařilo otevřít.", "Chyba prohlížeče", ERROR_MESSAGE);
        } catch (URISyntaxException | IOException ex) {
            JOptionPane.showMessageDialog(null, "Odkaz se nepodařilo otevřít.", "Chyba", ERROR_MESSAGE);
        }
    }
}
