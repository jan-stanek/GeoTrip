package cz.cvut.fit.geotrip;

import cz.cvut.fit.geotrip.controller.Controller;
import cz.cvut.fit.geotrip.model.Model;
import cz.cvut.fit.geotrip.view.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author jan
 */
public class GeoTrip {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) { }
       
        Model model = new Model();
        
        MainFrame mainFrame = new MainFrame();
        
        Controller controller = new Controller(model, mainFrame);
        
        mainFrame.registerController(controller);
        mainFrame.load();
        
        model.load();
        
        mainFrame.setVisible(true);
    }
    
}
