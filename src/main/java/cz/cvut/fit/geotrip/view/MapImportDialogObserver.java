package cz.cvut.fit.geotrip.view;

/**
 *
 * @author jan
 */
public class MapImportDialogObserver {
    
    private final MapImportDialog view;
    
    public MapImportDialogObserver(MapImportDialog view) {
        this.view = view;
    }
    
    public void update() {
        view.enableButtons();
        view.hideProgressBar();
    }
    
}
