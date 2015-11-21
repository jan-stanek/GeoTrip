package cz.cvut.fit.geotrip.view;

import java.util.List;

/**
 *
 * @author jan
 */
public class InstalledMapsObserver {
    
    private final MainFrame view;
    
    public InstalledMapsObserver(MainFrame view) {
        this.view = view;
    }
    
    public void update(List<String> maps) {
        view.setLookAndFeel();
        view.refreshMapList(maps);
    }
    
}
