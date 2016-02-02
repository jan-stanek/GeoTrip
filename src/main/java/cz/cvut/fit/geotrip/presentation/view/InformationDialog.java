package cz.cvut.fit.geotrip.presentation.view;

import javax.swing.JOptionPane;

public class InformationDialog {

    public void show(String title, String text) {
        JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
