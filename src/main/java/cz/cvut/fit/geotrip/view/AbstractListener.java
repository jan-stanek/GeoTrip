package cz.cvut.fit.geotrip.view;

import cz.cvut.fit.geotrip.controller.MainController;

/**
 *
 * @author jan
 */
public abstract class AbstractListener {
    
    protected final MainController controller;

    public AbstractListener(MainController controller) {
        this.controller = controller;
    }
    
}
