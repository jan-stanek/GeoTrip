package cz.cvut.fit.geotrip.view;

import org.mapsforge.core.model.LatLong;

/**
 *
 * @author jan
 */
public class CenterMapObserver {
    
    private final MainFrame view;
    
    public CenterMapObserver(MainFrame view) {
        this.view = view;
    }
    
    public void update(LatLong coordinates) {
        view.setMapPosition(coordinates);
    }
    
}
