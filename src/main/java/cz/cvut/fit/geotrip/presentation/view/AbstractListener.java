package cz.cvut.fit.geotrip.presentation.view;

import cz.cvut.fit.geotrip.presentation.controller.MainController;

public abstract class AbstractListener {

    protected final MainController controller;

    public AbstractListener(MainController controller) {
        this.controller = controller;
    }

}
