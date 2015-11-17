package cz.cvut.fit.geotrip.view;

import javax.swing.JOptionPane;

/**
 *
 * @author jan
 */
public class InformationDialogObserver {

    public void update(String title, String text) {
        JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
}
