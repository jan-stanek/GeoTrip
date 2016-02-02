package cz.cvut.fit.geotrip.presentation.view;

public class PlanningDialogObserver {

    private final MainFrame view;
    private PlanningDialog dialog;

    public PlanningDialogObserver(MainFrame view) {
        this.view = view;
    }

    public void show() {
        dialog = new PlanningDialog(view, true);
        dialog.setVisible(true);
    }

    public void hide() {
        dialog.setVisible(false);
    }
}
