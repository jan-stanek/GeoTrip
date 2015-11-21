package cz.cvut.fit.geotrip.view;

import cz.cvut.fit.geotrip.controller.MainController;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import org.mapsforge.map.swing.view.MapView;

/**
 *
 * @author jan
 */
public class MapViewMouseListener extends AbstractListener implements MouseListener {

    private final MapView mapView;
    
    public MapViewMouseListener(MainController controller, MapView mapView) {
        super(controller);
        this.mapView = mapView;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        controller.getSelectedLayer(mapView, controller.getModel().getLayers(), e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
    
}
