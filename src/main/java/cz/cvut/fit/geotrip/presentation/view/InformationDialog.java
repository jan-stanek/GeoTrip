package cz.cvut.fit.geotrip.presentation.view;

import javax.swing.JOptionPane;

public class InformationDialog {

    private static InformationDialog instance = new InformationDialog();

    private InformationDialog() {
    }

    public static InformationDialog getInstance() {
        return instance;
    }

    public void show(String title, String text) {
        JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
