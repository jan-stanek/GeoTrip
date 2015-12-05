package cz.cvut.fit.geotrip.presentation.controller;

import cz.cvut.fit.geotrip.business.MainModel;
import cz.cvut.fit.geotrip.presentation.view.MapImportDialog;
import java.io.File;

/**
 *
 * @author jan
 */
public class MapImportController {
    private final MainModel model;
    private final MapImportDialog view;


    public MapImportController(MainModel model, MapImportDialog view) {
        this.model = model;
        this.view = view;
    }
    
    public void importMap(File  mapFile, File osmFile) {
        view.disableButtons();
        view.showProgressBar();
        model.importMap(mapFile, osmFile);
    }
    
    public void selectMap() {
        view.showMapFileChooser();
    }
    
    public void selectOsm() {
        view.showOsmFileChooser();
    }
    
    public void setChosenMap(File mapFile) {
        if (mapFile != null) {
            view.setChosenMapName(mapFile.getName());
            view.setCurrentDirectory(mapFile.getParentFile());
        }
    }
    
    public void setChosenOsm(File osmFile) {
        if (osmFile != null) {
            view.setChosenOsmName(osmFile.getName());
            view.setCurrentDirectory(osmFile.getParentFile());
        }
    }
    
    public void closeDialog() {
        view.dispose();
    }
}
