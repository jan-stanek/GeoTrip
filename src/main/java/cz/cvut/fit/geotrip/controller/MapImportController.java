/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.controller;

import cz.cvut.fit.geotrip.model.MainModel;
import cz.cvut.fit.geotrip.view.MainFrame;
import cz.cvut.fit.geotrip.view.MapImportDialog;
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
    
    public void importMap(File map, File route) {
        // disable buttons
    }
}
