/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.presentation.controller;

import com.sun.glass.ui.CommonDialogs;
import cz.cvut.fit.geotrip.business.MainModel;
import cz.cvut.fit.geotrip.presentation.view.MainFrame;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author jan
 */
public class GpxExportAction extends AbstractAction {

    MainModel model;
    MainFrame view;
    
    public GpxExportAction(String name, MainFrame view, MainModel model) {
        super(name);
        this.view = view;
        this.model = model; 
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("GPX format", "gpx"));
        int ret = fileChooser.showSaveDialog(view);
        if (ret == JFileChooser.APPROVE_OPTION) {
            String file = fileChooser.getSelectedFile().getAbsolutePath();
            if (!file.endsWith(".gpx"))
                file += ".gpx";
            model.exportToGpx(new File(file));
        }
    }
    
}
