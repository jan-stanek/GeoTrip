package cz.cvut.fit.geotrip.controller;

import cz.cvut.fit.geotrip.model.Model;
import cz.cvut.fit.geotrip.view.MainFrame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author jan
 */
public class MapSelectAction extends AbstractAction {
    
    private Controller controller;
    private String mapName;
    
    public MapSelectAction(Controller controller, String mapName) {
        super(mapName);
        this.controller = controller;
        this.mapName = mapName;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        controller.getModel().changeMap(mapName);
    }
    
}
