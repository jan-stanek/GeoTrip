/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.view;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import cz.cvut.fit.geotrip.controller.MapImportController;
import cz.cvut.fit.geotrip.model.MainModel;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author jan
 */
public class MapImportDialog extends javax.swing.JDialog {

    MainModel model;
    MapImportController controller;
    
    /**
     * Creates new form MapImportDialog
     */
    public MapImportDialog(java.awt.Frame parent, boolean modal, MainModel model) {
        super(parent, modal);
        
        this.model = model;
        
        initComponents();
        
        this.setLocation(parent.getLocation().x + parent.getSize().width / 2 - this.getSize().width / 2, 
                parent.getLocation().y + parent.getSize().height / 2 - this.getSize().height / 2);
    }

    public void registerController(MapImportController controller) {
        this.controller = controller;
    }
 /*   
    private void initGraphHoppers(final String mapName) {
        final Thread t1 = initGraphHopper(mapName, "car");
        final Thread t2 = initGraphHopper(mapName, "bike");
        final Thread t3 = initGraphHopper(mapName, "foot");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                t1.start();
                t2.start();
                t3.start();

                try {
                    t1.join();
                    t2.join();
                    t3.join();
                } catch (InterruptedException ex) { }

                dispose();
            }
        });
    }
    
    private Thread initGraphHopper(final String mapName, final String vehicle) {
        return new Thread() {
            @Override
            public void run() {
                GraphHopper gh = new GraphHopper().setGraphHopperLocation("data/gh/" + mapName + "/" + vehicle).setEncodingManager(new EncodingManager(vehicle)).setOSMFile(new File("data/maps/" + mapName + ".osm.pbf").getAbsolutePath()).forDesktop();
                gh.importOrLoad();
            }
        };
    }
    */
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        labelMapFile = new javax.swing.JLabel();
        labelRouteFile = new javax.swing.JLabel();
        textMapFile = new javax.swing.JTextField();
        textRouteFile = new javax.swing.JTextField();
        buttonSelectMap = new javax.swing.JButton();
        buttonSelectRoute = new javax.swing.JButton();
        buttonImport = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        labelMapFile.setText("Mapový soubor:");

        labelRouteFile.setText("Routovací soubor:");

        textMapFile.setText("jTextField1");

        textRouteFile.setText("jTextField1");

        buttonSelectMap.setText("Vybrat");

        buttonSelectRoute.setText("Vybrat");

        buttonImport.setText("Importovat");
        buttonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonImportActionPerformed(evt);
            }
        });

        buttonCancel.setText("Zrušit");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonImport))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelMapFile)
                                .addGap(14, 14, 14)
                                .addComponent(textMapFile, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonSelectMap))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelRouteFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textRouteFile, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonSelectRoute)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textMapFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelMapFile)
                    .addComponent(buttonSelectMap))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelRouteFile)
                    .addComponent(textRouteFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSelectRoute))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonImport))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonImportActionPerformed
        controller.importMap(null, null);
    }//GEN-LAST:event_buttonImportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonImport;
    private javax.swing.JButton buttonSelectMap;
    private javax.swing.JButton buttonSelectRoute;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel labelMapFile;
    private javax.swing.JLabel labelRouteFile;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField textMapFile;
    private javax.swing.JTextField textRouteFile;
    // End of variables declaration//GEN-END:variables
}
