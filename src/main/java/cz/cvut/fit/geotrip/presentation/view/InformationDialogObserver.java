package cz.cvut.fit.geotrip.presentation.view;

import javax.swing.JOptionPane;

/**
 *
 * @author jan
 */
public class InformationDialogObserver {

    public void show(String title, String text) {
        JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
}
