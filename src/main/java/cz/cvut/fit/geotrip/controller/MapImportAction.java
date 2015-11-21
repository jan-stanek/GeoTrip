package cz.cvut.fit.geotrip.controller;

import cz.cvut.fit.geotrip.model.MainModel;
import cz.cvut.fit.geotrip.view.MainFrame;
import cz.cvut.fit.geotrip.view.MapImportDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author jan
 */
public class MapImportAction extends AbstractAction {

    MainModel model;
    MainFrame view;
    
    public MapImportAction(String name, MainFrame view, MainModel model) {
        super(name);
        this.view = view;
        this.model = model; 
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        MapImportDialog mapImportDialog = new MapImportDialog(view, true, model);
        MapImportController mapImportController = new MapImportController(model, mapImportDialog);
        mapImportDialog.registerController(mapImportController);
        mapImportDialog.setVisible(true);  
    }
    
}
