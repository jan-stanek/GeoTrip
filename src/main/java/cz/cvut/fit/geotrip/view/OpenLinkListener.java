/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.view;

import cz.cvut.fit.geotrip.controller.MainController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author jan
 */
public class OpenLinkListener extends AbstractListener implements ActionListener {

    String link;
    
    public OpenLinkListener(MainController controller, String link) {
        super(controller);
        this.link = link;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.openUrl(link);
    }
    
}
