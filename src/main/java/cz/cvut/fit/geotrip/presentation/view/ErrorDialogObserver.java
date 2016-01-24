package cz.cvut.fit.geotrip.presentation.view;

import javax.swing.JOptionPane;

/**
 *
 * @author jan
 */
public class ErrorDialogObserver {

    public void show(String title, String text) {
        JOptionPane.showMessageDialog(null, text, title, JOptionPane.ERROR_MESSAGE);
    }
    
}
