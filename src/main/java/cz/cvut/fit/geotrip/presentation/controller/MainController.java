package cz.cvut.fit.geotrip.presentation.controller;

import cz.cvut.fit.geotrip.data.entities.CacheContainer;
import cz.cvut.fit.geotrip.business.MainModel;
import cz.cvut.fit.geotrip.business.RoutingTypes;
import cz.cvut.fit.geotrip.presentation.view.MainFrame;
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
    
    public void planTrip(String distanceStr, RoutingTypes vehicle, boolean found, boolean containerMicro, boolean containerSmall, boolean containerRegular,
            boolean containerLarge, boolean containerOther, int difficultyLow, int difficultyHigh, int terrainLow, int terrainHigh,
            boolean containerPriorityI, boolean containerPriorityL, boolean containerPriorityH, boolean difficultyPriorityI, boolean difficultyPriorityL,
            boolean difficultyPriorityH, boolean terrainPriorityI, boolean terrainPriorityL, boolean terrainPriorityH) {
        
        int distance = Integer.parseInt(distanceStr);
        
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
        
        int containerPriority = containerPriorityI ? 0 : (containerPriorityL ? -1 : 1);
        int difficultyPriority = difficultyPriorityI ? 0 : (difficultyPriorityL ? -1 : 1);
        int terrainPriority = terrainPriorityI ? 0 : (terrainPriorityL ? -1 : 1);
        
        view.hideCacheInfo();
        view.hideTripInfo();
        
        if (model.planTrip(distance * 1000, vehicle, found, container, difficultyLow, difficultyHigh,
                terrainLow, terrainHigh, containerPriority, difficultyPriority, terrainPriority)) {
            view.setExportEnabled(true);
            view.showTripInfo(model.getTripLength(), model.getTripTime(), model.getTripCaches());
            view.zoomTo(model.getBoundingBox());
        }
        else {
            view.setExportEnabled(false);
        }
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
