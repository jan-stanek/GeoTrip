package cz.cvut.fit.geotrip.presentation.view;

import javax.swing.JOptionPane;

public class ErrorDialog {

    public void show(String title, String text) {
        JOptionPane.showMessageDialog(null, text, title, JOptionPane.ERROR_MESSAGE);
    }
}
