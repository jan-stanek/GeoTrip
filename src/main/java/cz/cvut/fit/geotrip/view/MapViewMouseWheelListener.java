/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.view;

import cz.cvut.fit.geotrip.controller.Controller;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 *
 * @author jan
 */
public class MapViewMouseWheelListener extends AbstractListener implements MouseWheelListener {

    public MapViewMouseWheelListener(Controller controller) {
        super(controller);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        controller.changeZoom(e.getWheelRotation());
    }

}
