package cz.cvut.fit.geotrip.view;

import cz.cvut.fit.geotrip.controller.Controller;

/**
 *
 * @author jan
 */
public abstract class AbstractListener {
    
    protected final Controller controller;

    public AbstractListener(Controller controller) {
        this.controller = controller;
    }
    
}
