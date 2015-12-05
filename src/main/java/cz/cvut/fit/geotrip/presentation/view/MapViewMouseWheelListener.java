package cz.cvut.fit.geotrip.presentation.view;

import cz.cvut.fit.geotrip.presentation.controller.MainController;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 *
 * @author jan
 */
public class MapViewMouseWheelListener extends AbstractListener implements MouseWheelListener {

    public MapViewMouseWheelListener(MainController controller) {
        super(controller);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        controller.changeZoom(e.getWheelRotation());
    }

}
