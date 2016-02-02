package cz.cvut.fit.geotrip.presentation.view;

import cz.cvut.fit.geotrip.data.entities.GeoPoint;

public class CenterMapObserver {

    private final MainFrame view;

    public CenterMapObserver(MainFrame view) {
        this.view = view;
    }

    public void update(GeoPoint coordinates) {
        view.setMapPosition(coordinates);
    }

}
