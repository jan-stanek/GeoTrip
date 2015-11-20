package cz.cvut.fit.geotrip.controller;

import cz.cvut.fit.geotrip.model.MainModel;
import cz.cvut.fit.geotrip.view.MainFrame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author jan
 */
public class MapSelectAction extends AbstractAction {
    
    private MainController controller;
    private String mapName;
    
    public MapSelectAction(MainController controller, String mapName) {
        super(mapName);
        this.controller = controller;
        this.mapName = mapName;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        controller.getModel().changeMap(mapName);
    }
    
}
