package cz.cvut.fit.geotrip.presentation.controller;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author jan
 */
public class MapSelectAction extends AbstractAction {
    
    private final MainController controller;
    private final String mapName;
    
    public MapSelectAction(String mapName, MainController controller) {
        super(mapName);
        this.controller = controller;
        this.mapName = mapName;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        controller.getModel().changeMap(mapName);
    }
    
}
