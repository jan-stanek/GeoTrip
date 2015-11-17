/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.controller;

import cz.cvut.fit.geotrip.data.GeoPoint;
import cz.cvut.fit.geotrip.model.Model;
import cz.cvut.fit.geotrip.view.MainFrame;
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
public class Controller {

    private final Model model;
    private final MainFrame view;
    
    public Controller(Model model, MainFrame view) {
        this.model = model;
        this.view = view;
    }

    public Model getModel() {
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

    public void changeZoom(int rotation) {
        view.setZoomLevel(view.getZoomLevel() - rotation);
    }
    
    public GeoPoint getRef() {
        return model.getRefPoint();
    }
    
    public void planTrip(int distance, String vehicle, final boolean found, final int container,
            final int difficultyLow, final int difficultyHigh, final int terrainLow, final int terrainHigh) {
        view.hideCacheInfo();
        model.filter(distance, vehicle, found, container, difficultyLow, difficultyHigh, terrainLow, terrainHigh);
        view.zoomMapTo(model.getBoundingBox());
    }
    
    public void addMaps() {
        for (String s : model.getMapList())
            view.addMapItem(s);
    }
}
