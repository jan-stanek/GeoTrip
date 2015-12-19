package cz.cvut.fit.geotrip;

import cz.cvut.fit.geotrip.presentation.controller.MainController;
import cz.cvut.fit.geotrip.business.MainModel;
import cz.cvut.fit.geotrip.presentation.view.MainFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author jan
 */
public class GeoTrip {

    public static String DATA_DIRECTORY;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) { }
       
        DATA_DIRECTORY = args[0];
        
        MainModel model = new MainModel();
        MainFrame mainFrame = new MainFrame(model);
        MainController controller = new MainController(model, mainFrame);
        
        mainFrame.registerController(controller);
        mainFrame.init();
        
        mainFrame.setVisible(true);
    }
    
}
