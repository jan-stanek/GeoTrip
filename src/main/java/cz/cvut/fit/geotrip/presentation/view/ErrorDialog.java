package cz.cvut.fit.geotrip.presentation.view;

import javax.swing.JOptionPane;

public class ErrorDialog {

    private static ErrorDialog instance = new ErrorDialog();

    private ErrorDialog() {
    }

    public static ErrorDialog getInstance() {
        return instance;
    }
    
    public void show(String title, String text) {
        JOptionPane.showMessageDialog(null, text, title, JOptionPane.ERROR_MESSAGE);
    }
}
