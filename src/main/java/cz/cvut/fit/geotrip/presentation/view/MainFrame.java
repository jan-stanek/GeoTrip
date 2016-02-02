package cz.cvut.fit.geotrip.presentation.view;

import com.jidesoft.swing.RangeSlider;
import cz.cvut.fit.geotrip.presentation.controller.MainController;
import cz.cvut.fit.geotrip.presentation.controller.MapImportAction;
import cz.cvut.fit.geotrip.presentation.controller.MapSelectAction;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.business.MainModel;
import cz.cvut.fit.geotrip.business.RoutingTypes;
import cz.cvut.fit.geotrip.business.TripTypes;
import cz.cvut.fit.geotrip.data.entities.GeoPoint;
import cz.cvut.fit.geotrip.presentation.controller.GpxExportAction;
import cz.cvut.fit.geotrip.utils.Texts;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.swing.controller.MapViewComponentListener;
import org.mapsforge.map.swing.controller.MouseEventListener;
import org.mapsforge.map.swing.view.MapView;

public class MainFrame extends javax.swing.JFrame {

    private final MainModel model;
    private MainController controller;

    private static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;

    private MapView mapView;
    private Model mapViewModel;

    private final byte ZOOM_MIN = 6;
    private final byte ZOOM_MAX = 20;
    private final byte ZOOM_DEFAULT = 13;

    private RangeSlider sliderDifficulty;
    private RangeSlider sliderTerrain;

    private final GpxExportAction gpxExportAction;
    private final MapImportAction mapImportAction;
    private final List<JRadioButtonMenuItem> mapMenuItems;

    public MainFrame(MainModel model) {
        initComponents();
        createRangeSliders();

        this.model = model;

        gpxExportAction = new GpxExportAction(Texts.getInstance().getLocalizedText("menuFileExport"), this, model);
        menuExport.setAction(gpxExportAction);
        menuExport.setEnabled(false);

        mapImportAction = new MapImportAction(Texts.getInstance().getLocalizedText("menuMapImport"), this, model);
        menuImportMap.setAction(mapImportAction);

        mapMenuItems = new LinkedList<>();

        model.registerInstalledMapsObserver(new InstalledMapsObserver(this));
        model.registerCenterMapObserver(new CenterMapObserver(this));
        model.registerPlanningDialogObserver(new PlanningDialogObserver(this));
    }

    public void init() {
        hideCacheInfo();
        hideTripInfo();
        createMapView();
        setIcon();
    }

    public void registerController(MainController controller) {
        this.controller = controller;
    }

    private void setIcon(URL img) {
        ImageIcon icon = new ImageIcon(img);
        this.setIconImage(icon.getImage());
    }

    private void createMapView() {
        panelRight.setBorder(new CompoundBorder(new EmptyBorder(6, 2, 2, 2), panelRight.getBorder()));

        mapView = new MapView();
        mapViewModel = mapView.getModel();
        mapView.getMapScaleBar().setVisible(true);

        model.setLayers(mapView.getLayerManager().getLayers());

        MouseEventListener mouseEventListener = new MouseEventListener(mapViewModel);
        MapViewMouseListener mapViewMouseListener = new MapViewMouseListener(controller, mapView);
        MapViewMouseWheelListener mapViewMouseWheelListener = new MapViewMouseWheelListener(controller);
        mapView.addComponentListener(new MapViewComponentListener(mapView, mapViewModel.mapViewDimension));
        mapView.addMouseMotionListener(mouseEventListener);
        mapView.addMouseListener(mapViewMouseListener);
        mapView.addMouseListener(mouseEventListener);
        mapView.addMouseWheelListener(mapViewMouseWheelListener);

        mapView.setSize(panelMap.getWidth(), panelMap.getHeight());

        mapViewModel.mapViewPosition.setZoomLevelMin(ZOOM_MIN);
        mapViewModel.mapViewPosition.setZoomLevelMax(ZOOM_MAX);

        panelMap.add(mapView);

        sliderZoom.setMinimum(ZOOM_MIN);
        sliderZoom.setMaximum(ZOOM_MAX);
        sliderZoom.setValue(ZOOM_DEFAULT);

        model.setMapViewPosition(mapViewModel.mapViewPosition);
        model.init();
    }

    public void setExportEnabled(boolean enabled) {
        menuExport.setEnabled(enabled);
    }

    private void createRangeSliders() {
        Hashtable labelTable = new Hashtable();
        labelTable.put(1, new JLabel("1"));
        labelTable.put(3, new JLabel("2"));
        labelTable.put(5, new JLabel("3"));
        labelTable.put(7, new JLabel("4"));
        labelTable.put(9, new JLabel("5"));

        sliderDifficulty = new RangeSlider(1, 9, 1, 9);
        sliderDifficulty.setPaintTicks(true);
        sliderDifficulty.setPaintLabels(true);
        sliderDifficulty.setMajorTickSpacing(2);
        sliderDifficulty.setMinorTickSpacing(1);
        sliderDifficulty.setSize(267, 40);
        sliderDifficulty.setLocation(6, 16);
        sliderDifficulty.setLabelTable(labelTable);
        sliderDifficulty.setFocusable(false);
        panelFilterDifficulty.add(sliderDifficulty);

        sliderTerrain = new RangeSlider(1, 9, 1, 9);
        sliderTerrain.setPaintTicks(true);
        sliderTerrain.setPaintLabels(true);
        sliderTerrain.setMajorTickSpacing(2);
        sliderTerrain.setMinorTickSpacing(1);
        sliderTerrain.setSize(267, 40);
        sliderTerrain.setLocation(6, 16);
        sliderTerrain.setLabelTable(labelTable);
        sliderTerrain.setFocusable(false);
        panelFilterTerrain.add(sliderTerrain);
    }

    public void zoomTo(BoundingBox boundingBox) {
        setMapPosition(boundingBox.getCenterPoint());
        setZoomLevel(LatLongUtils.zoomForBounds(mapViewModel.mapViewDimension.getDimension(),
                boundingBox, mapViewModel.displayModel.getTileSize()));
    }

    public void setMapPosition(GeoPoint coordinates) {
        setMapPosition(new LatLong(coordinates.getLat(), coordinates.getLon()));
    }

    public void setMapPosition(LatLong coordinates) {
        mapViewModel.mapViewPosition.setCenter(coordinates);
    }

    public int getZoomLevel() {
        return mapViewModel.mapViewPosition.getZoomLevel();
    }

    public void setZoomLevel(int zoom) {
        mapViewModel.mapViewPosition.setZoomLevel((byte) zoom);
        sliderZoom.setValue(zoom);
    }

    public void showCacheInfo(GeoCache cache) {
        textName.setText(cache.getName());
        textName.setCaretPosition(0);

        textCoordinates.setText(cache.getCoordinatesString());
        textContainer.setText(cache.getContainer().getName());
        textDifficulty.setText(cache.getDifficultyString());
        textTerrain.setText(cache.getTerrainString());

        buttonLink.setText("<html><a href=\"\">" + cache.getId() + "</a></html>");
        for (ActionListener al : buttonLink.getActionListeners()) {
            buttonLink.removeActionListener(al);
        }
        buttonLink.addActionListener(new OpenLinkListener(controller, cache.getLink()));

        panelCacheInfo.setVisible(true);
    }

    public void hideCacheInfo() {
        panelCacheInfo.setVisible(false);
    }

    public void showTripInfo(String length, String time, String caches) {
        textTripLength.setText(length);
        textTripTime.setText(time);
        textTripCaches.setText(caches);
        panelTripInfo.setVisible(true);
    }

    public void hideTripInfo() {
        panelTripInfo.setVisible(false);
    }

    public void refreshMapList(List<String> maps) {
        for (JRadioButtonMenuItem item : mapMenuItems) {
            menuMap.remove(item);
        }
        mapMenuItems.clear();

        ButtonGroup buttonGroup = new ButtonGroup();
        for (String map : maps) {
            MapSelectAction mapSelectAction = new MapSelectAction(map, controller);
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(mapSelectAction);
            if (map.equals(model.getSelectedMap())) {
                menuItem.setSelected(true);
            }
            menuMap.add(menuItem);
            buttonGroup.add(menuItem);
            mapMenuItems.add(menuItem);
        }
    }

    public void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
    }

    private void setIcon() {
        List<Image> icons = new ArrayList<>();
        icons.add(new ImageIcon(getClass().getClassLoader().getResource("icon-small.png")).getImage());
        icons.add(new ImageIcon(getClass().getClassLoader().getResource("icon-medium.png")).getImage());
        icons.add(new ImageIcon(getClass().getClassLoader().getResource("icon.png")).getImage());
        setIconImages(icons);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupState = new javax.swing.ButtonGroup();
        groupContainer = new javax.swing.ButtonGroup();
        groupDifficulty = new javax.swing.ButtonGroup();
        groupTerrain = new javax.swing.ButtonGroup();
        panelLeft = new javax.swing.JPanel();
        panelTrip = new javax.swing.JPanel();
        labelLength = new javax.swing.JLabel();
        fieldDelka = new javax.swing.JTextField();
        labelRouting = new javax.swing.JLabel();
        comboRouting = new javax.swing.JComboBox();
        labelKm = new javax.swing.JLabel();
        comboTripType = new javax.swing.JComboBox();
        labelTripType = new javax.swing.JLabel();
        panelFilter = new javax.swing.JPanel();
        panelFilterState = new javax.swing.JPanel();
        radioAll = new javax.swing.JRadioButton();
        radioNotFound = new javax.swing.JRadioButton();
        panelFilterContainer = new javax.swing.JPanel();
        checkMicro = new javax.swing.JCheckBox();
        checkSmall = new javax.swing.JCheckBox();
        checkRegular = new javax.swing.JCheckBox();
        checkLarge = new javax.swing.JCheckBox();
        checkOther = new javax.swing.JCheckBox();
        panelFilterDifficulty = new javax.swing.JPanel();
        panelFilterTerrain = new javax.swing.JPanel();
        panelPreferences = new javax.swing.JPanel();
        panelPreferencesContainer = new javax.swing.JPanel();
        radioContainerSmall = new javax.swing.JRadioButton();
        radioContainerLarge = new javax.swing.JRadioButton();
        radioContainerIgnore = new javax.swing.JRadioButton();
        panelPreferencesDifficulty = new javax.swing.JPanel();
        radioDifficultySmall = new javax.swing.JRadioButton();
        radioDifficultyGreat = new javax.swing.JRadioButton();
        radioDifficultyIgnore = new javax.swing.JRadioButton();
        panelPreferencesTerrain = new javax.swing.JPanel();
        radioTerrainLight = new javax.swing.JRadioButton();
        radioTerrainDifficult = new javax.swing.JRadioButton();
        radioTerrainIgnore = new javax.swing.JRadioButton();
        buttonPlan = new javax.swing.JButton();
        panelRight = new javax.swing.JPanel();
        panelMap = new javax.swing.JPanel();
        sliderZoom = new javax.swing.JSlider();
        buttonZoomPlus = new javax.swing.JButton();
        buttonZoomMinus = new javax.swing.JButton();
        panelCacheInfo = new javax.swing.JPanel();
        textName = new javax.swing.JTextField();
        textCoordinates = new javax.swing.JTextField();
        labelContainer = new javax.swing.JLabel();
        textContainer = new javax.swing.JTextField();
        labelDifficulty = new javax.swing.JLabel();
        textDifficulty = new javax.swing.JTextField();
        labelTerrain = new javax.swing.JLabel();
        textTerrain = new javax.swing.JTextField();
        buttonLink = new javax.swing.JButton();
        panelTripInfo = new javax.swing.JPanel();
        labelTripLength = new javax.swing.JLabel();
        textTripLength = new javax.swing.JTextField();
        labelTripTime = new javax.swing.JLabel();
        textTripTime = new javax.swing.JTextField();
        labelTripKm = new javax.swing.JLabel();
        labelTripCaches = new javax.swing.JLabel();
        textTripCaches = new javax.swing.JTextField();
        menu = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuExport = new javax.swing.JMenuItem();
        menuMap = new javax.swing.JMenu();
        menuImportMap = new javax.swing.JMenuItem();
        separator = new javax.swing.JPopupMenu.Separator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GeoTrip");
        setMinimumSize(new java.awt.Dimension(880, 630));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                MainFrame.this.componentResized(evt);
            }
        });

        panelTrip.setBorder(javax.swing.BorderFactory.createTitledBorder("Výlet"));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        labelLength.setText(bundle.getString("labelLength")); // NOI18N

        fieldDelka.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fieldDelka.setText("0");

        labelRouting.setText(bundle.getString("labelVehicle")); // NOI18N

        comboRouting.setModel(new DefaultComboBoxModel(RoutingTypes.values()));
        comboRouting.setToolTipText("");

        labelKm.setText("km");

        comboTripType.setModel(new DefaultComboBoxModel(TripTypes.values())
        );

        labelTripType.setText(bundle.getString("labelTripType")); // NOI18N

        javax.swing.GroupLayout panelTripLayout = new javax.swing.GroupLayout(panelTrip);
        panelTrip.setLayout(panelTripLayout);
        panelTripLayout.setHorizontalGroup(
            panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTripLayout.createSequentialGroup()
                .addGroup(panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelLength)
                    .addComponent(labelRouting)
                    .addComponent(labelTripType))
                .addGap(4, 4, 4)
                .addGroup(panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTripLayout.createSequentialGroup()
                        .addComponent(fieldDelka)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelKm))
                    .addComponent(comboRouting, 0, 223, Short.MAX_VALUE)
                    .addComponent(comboTripType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        panelTripLayout.setVerticalGroup(
            panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTripLayout.createSequentialGroup()
                .addGroup(panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldDelka, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLength)
                    .addComponent(labelKm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboRouting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelRouting))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboTripType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTripType)))
        );

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("panelFilter"))); // NOI18N

        panelFilterState.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("panelFilterState"))); // NOI18N

        groupState.add(radioAll);
        radioAll.setSelected(true);
        radioAll.setText(bundle.getString("radioFilterStateAll")); // NOI18N

        groupState.add(radioNotFound);
        radioNotFound.setText(bundle.getString("radioFilterStateNotFound")); // NOI18N

        javax.swing.GroupLayout panelFilterStateLayout = new javax.swing.GroupLayout(panelFilterState);
        panelFilterState.setLayout(panelFilterStateLayout);
        panelFilterStateLayout.setHorizontalGroup(
            panelFilterStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterStateLayout.createSequentialGroup()
                .addComponent(radioAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioNotFound)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFilterStateLayout.setVerticalGroup(
            panelFilterStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(radioAll)
                .addComponent(radioNotFound))
        );

        panelFilterContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("panelContainer"))); // NOI18N

        checkMicro.setSelected(true);
        checkMicro.setText(bundle.getString("containerMicro")); // NOI18N

        checkSmall.setSelected(true);
        checkSmall.setText(bundle.getString("containerSmall")); // NOI18N

        checkRegular.setSelected(true);
        checkRegular.setText(bundle.getString("containerRegular")); // NOI18N

        checkLarge.setSelected(true);
        checkLarge.setText(bundle.getString("containerLarge")); // NOI18N
        checkLarge.setActionCommand(bundle.getString("containerLarge")); // NOI18N

        checkOther.setSelected(true);
        checkOther.setText(bundle.getString("containerOther")); // NOI18N

        javax.swing.GroupLayout panelFilterContainerLayout = new javax.swing.GroupLayout(panelFilterContainer);
        panelFilterContainer.setLayout(panelFilterContainerLayout);
        panelFilterContainerLayout.setHorizontalGroup(
            panelFilterContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterContainerLayout.createSequentialGroup()
                .addComponent(checkMicro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkSmall)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkRegular)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkLarge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkOther))
        );
        panelFilterContainerLayout.setVerticalGroup(
            panelFilterContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(checkMicro)
                .addComponent(checkSmall)
                .addComponent(checkRegular)
                .addComponent(checkLarge)
                .addComponent(checkOther))
        );

        panelFilterDifficulty.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("panelDifficulty"))); // NOI18N

        javax.swing.GroupLayout panelFilterDifficultyLayout = new javax.swing.GroupLayout(panelFilterDifficulty);
        panelFilterDifficulty.setLayout(panelFilterDifficultyLayout);
        panelFilterDifficultyLayout.setHorizontalGroup(
            panelFilterDifficultyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFilterDifficultyLayout.setVerticalGroup(
            panelFilterDifficultyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        panelFilterTerrain.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("panelTerrain"))); // NOI18N
        panelFilterTerrain.setPreferredSize(new java.awt.Dimension(286, 63));

        javax.swing.GroupLayout panelFilterTerrainLayout = new javax.swing.GroupLayout(panelFilterTerrain);
        panelFilterTerrain.setLayout(panelFilterTerrainLayout);
        panelFilterTerrainLayout.setHorizontalGroup(
            panelFilterTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFilterTerrainLayout.setVerticalGroup(
            panelFilterTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFilterTerrain, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
            .addComponent(panelFilterDifficulty, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFilterState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFilterContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addComponent(panelFilterState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFilterContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFilterDifficulty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelFilterTerrain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelPreferences.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("panelPreferences"))); // NOI18N

        panelPreferencesContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("panelContainer"))); // NOI18N

        groupContainer.add(radioContainerSmall);
        radioContainerSmall.setText(bundle.getString("radioPreferencesSmall")); // NOI18N

        groupContainer.add(radioContainerLarge);
        radioContainerLarge.setText(bundle.getString("radioPreferencesLarge")); // NOI18N

        groupContainer.add(radioContainerIgnore);
        radioContainerIgnore.setSelected(true);
        radioContainerIgnore.setText(bundle.getString("radioPreferencesIgnore")); // NOI18N

        javax.swing.GroupLayout panelPreferencesContainerLayout = new javax.swing.GroupLayout(panelPreferencesContainer);
        panelPreferencesContainer.setLayout(panelPreferencesContainerLayout);
        panelPreferencesContainerLayout.setHorizontalGroup(
            panelPreferencesContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferencesContainerLayout.createSequentialGroup()
                .addComponent(radioContainerIgnore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioContainerSmall)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioContainerLarge)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPreferencesContainerLayout.setVerticalGroup(
            panelPreferencesContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferencesContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(radioContainerSmall)
                .addComponent(radioContainerLarge)
                .addComponent(radioContainerIgnore))
        );

        panelPreferencesDifficulty.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("panelDifficulty"))); // NOI18N

        groupDifficulty.add(radioDifficultySmall);
        radioDifficultySmall.setText(bundle.getString("radioPreferencesSmall")); // NOI18N

        groupDifficulty.add(radioDifficultyGreat);
        radioDifficultyGreat.setText(bundle.getString("radioPreferencesHigh")); // NOI18N

        groupDifficulty.add(radioDifficultyIgnore);
        radioDifficultyIgnore.setSelected(true);
        radioDifficultyIgnore.setText(bundle.getString("radioPreferencesIgnore")); // NOI18N

        javax.swing.GroupLayout panelPreferencesDifficultyLayout = new javax.swing.GroupLayout(panelPreferencesDifficulty);
        panelPreferencesDifficulty.setLayout(panelPreferencesDifficultyLayout);
        panelPreferencesDifficultyLayout.setHorizontalGroup(
            panelPreferencesDifficultyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferencesDifficultyLayout.createSequentialGroup()
                .addComponent(radioDifficultyIgnore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioDifficultySmall)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioDifficultyGreat)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPreferencesDifficultyLayout.setVerticalGroup(
            panelPreferencesDifficultyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferencesDifficultyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(radioDifficultySmall)
                .addComponent(radioDifficultyGreat)
                .addComponent(radioDifficultyIgnore))
        );

        panelPreferencesTerrain.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("panelTerrain"))); // NOI18N

        groupTerrain.add(radioTerrainLight);
        radioTerrainLight.setText(bundle.getString("radioPreferencesLight")); // NOI18N

        groupTerrain.add(radioTerrainDifficult);
        radioTerrainDifficult.setText(bundle.getString("radioPreferencesDifficult")); // NOI18N

        groupTerrain.add(radioTerrainIgnore);
        radioTerrainIgnore.setSelected(true);
        radioTerrainIgnore.setText(bundle.getString("radioPreferencesIgnore")); // NOI18N
        radioTerrainIgnore.setActionCommand(bundle.getString("radioPreferencesIgnore")); // NOI18N

        javax.swing.GroupLayout panelPreferencesTerrainLayout = new javax.swing.GroupLayout(panelPreferencesTerrain);
        panelPreferencesTerrain.setLayout(panelPreferencesTerrainLayout);
        panelPreferencesTerrainLayout.setHorizontalGroup(
            panelPreferencesTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferencesTerrainLayout.createSequentialGroup()
                .addComponent(radioTerrainIgnore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioTerrainLight)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioTerrainDifficult)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPreferencesTerrainLayout.setVerticalGroup(
            panelPreferencesTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferencesTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(radioTerrainLight)
                .addComponent(radioTerrainDifficult)
                .addComponent(radioTerrainIgnore))
        );

        javax.swing.GroupLayout panelPreferencesLayout = new javax.swing.GroupLayout(panelPreferences);
        panelPreferences.setLayout(panelPreferencesLayout);
        panelPreferencesLayout.setHorizontalGroup(
            panelPreferencesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPreferencesContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelPreferencesDifficulty, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelPreferencesTerrain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelPreferencesLayout.setVerticalGroup(
            panelPreferencesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferencesLayout.createSequentialGroup()
                .addComponent(panelPreferencesContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPreferencesDifficulty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPreferencesTerrain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        buttonPlan.setText(bundle.getString("buttonPlan")); // NOI18N
        buttonPlan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPlanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLeftLayout = new javax.swing.GroupLayout(panelLeft);
        panelLeft.setLayout(panelLeftLayout);
        panelLeftLayout.setHorizontalGroup(
            panelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonPlan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelTrip, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelPreferences, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelLeftLayout.setVerticalGroup(
            panelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLeftLayout.createSequentialGroup()
                .addComponent(panelTrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPreferences, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPlan)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        panelRight.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        sliderZoom.setMajorTickSpacing(1);
        sliderZoom.setMaximum(20);
        sliderZoom.setOrientation(javax.swing.JSlider.VERTICAL);
        sliderZoom.setSnapToTicks(true);
        sliderZoom.setValue(10);
        sliderZoom.setFocusable(false);
        sliderZoom.setOpaque(false);
        sliderZoom.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderZoomStateChanged(evt);
            }
        });

        buttonZoomPlus.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        buttonZoomPlus.setForeground(new java.awt.Color(0, 102, 255));
        buttonZoomPlus.setText("+");
        buttonZoomPlus.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        buttonZoomPlus.setContentAreaFilled(false);
        buttonZoomPlus.setFocusable(false);
        buttonZoomPlus.setPreferredSize(new java.awt.Dimension(20, 20));
        buttonZoomPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonZoomPlusActionPerformed(evt);
            }
        });

        buttonZoomMinus.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        buttonZoomMinus.setForeground(new java.awt.Color(0, 102, 255));
        buttonZoomMinus.setText("−");
        buttonZoomMinus.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        buttonZoomMinus.setContentAreaFilled(false);
        buttonZoomMinus.setFocusable(false);
        buttonZoomMinus.setPreferredSize(new java.awt.Dimension(20, 20));
        buttonZoomMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonZoomMinusActionPerformed(evt);
            }
        });

        panelCacheInfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        textName.setEditable(false);
        textName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        textName.setText("name");
        textName.setBorder(null);

        textCoordinates.setEditable(false);
        textCoordinates.setText("gps");
        textCoordinates.setBorder(null);

        labelContainer.setText(bundle.getString("infoContainer")); // NOI18N

        textContainer.setEditable(false);
        textContainer.setText("other");
        textContainer.setBorder(null);

        labelDifficulty.setText(bundle.getString("infoDifficulty")); // NOI18N

        textDifficulty.setEditable(false);
        textDifficulty.setText("0");
        textDifficulty.setBorder(null);

        labelTerrain.setText(bundle.getString("infoTerrain")); // NOI18N

        textTerrain.setEditable(false);
        textTerrain.setText("0");
        textTerrain.setBorder(null);

        buttonLink.setText("link");
        buttonLink.setBorder(null);
        buttonLink.setContentAreaFilled(false);
        buttonLink.setFocusable(false);
        buttonLink.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout panelCacheInfoLayout = new javax.swing.GroupLayout(panelCacheInfo);
        panelCacheInfo.setLayout(panelCacheInfoLayout);
        panelCacheInfoLayout.setHorizontalGroup(
            panelCacheInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCacheInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCacheInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCacheInfoLayout.createSequentialGroup()
                        .addComponent(labelContainer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labelDifficulty)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textDifficulty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labelTerrain)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textTerrain, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelCacheInfoLayout.createSequentialGroup()
                        .addGroup(panelCacheInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelCacheInfoLayout.createSequentialGroup()
                                .addComponent(textCoordinates, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonLink)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        panelCacheInfoLayout.setVerticalGroup(
            panelCacheInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCacheInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCacheInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textCoordinates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonLink))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCacheInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelContainer)
                    .addComponent(textContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDifficulty)
                    .addComponent(textDifficulty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTerrain)
                    .addComponent(textTerrain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelTripInfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        labelTripLength.setText(bundle.getString("tripLength")); // NOI18N

        textTripLength.setEditable(false);
        textTripLength.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textTripLength.setText("0");
        textTripLength.setBorder(null);

        labelTripTime.setText(bundle.getString("tripTime")); // NOI18N

        textTripTime.setEditable(false);
        textTripTime.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textTripTime.setText("0:00");
        textTripTime.setBorder(null);

        labelTripKm.setText("km");

        labelTripCaches.setText(bundle.getString("tripCaches")); // NOI18N

        textTripCaches.setEditable(false);
        textTripCaches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textTripCaches.setText("0");
        textTripCaches.setBorder(null);

        javax.swing.GroupLayout panelTripInfoLayout = new javax.swing.GroupLayout(panelTripInfo);
        panelTripInfo.setLayout(panelTripInfoLayout);
        panelTripInfoLayout.setHorizontalGroup(
            panelTripInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTripInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTripInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTripInfoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelTripInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelTripInfoLayout.createSequentialGroup()
                                .addComponent(labelTripTime)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textTripTime))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTripInfoLayout.createSequentialGroup()
                                .addComponent(labelTripLength)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textTripLength, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelTripKm))))
                    .addGroup(panelTripInfoLayout.createSequentialGroup()
                        .addComponent(labelTripCaches)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textTripCaches)))
                .addContainerGap())
        );
        panelTripInfoLayout.setVerticalGroup(
            panelTripInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTripInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTripInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTripLength)
                    .addComponent(textTripLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTripKm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTripInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTripTime)
                    .addComponent(textTripTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTripInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTripCaches)
                    .addComponent(textTripCaches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelMapLayout = new javax.swing.GroupLayout(panelMap);
        panelMap.setLayout(panelMapLayout);
        panelMapLayout.setHorizontalGroup(
            panelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMapLayout.createSequentialGroup()
                        .addGap(0, 221, Short.MAX_VALUE)
                        .addComponent(panelCacheInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelMapLayout.createSequentialGroup()
                        .addGroup(panelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonZoomMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonZoomPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sliderZoom, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelTripInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelMapLayout.setVerticalGroup(
            panelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMapLayout.createSequentialGroup()
                        .addComponent(buttonZoomPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sliderZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelTripInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonZoomMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelCacheInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelRightLayout = new javax.swing.GroupLayout(panelRight);
        panelRight.setLayout(panelRightLayout);
        panelRightLayout.setHorizontalGroup(
            panelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelRightLayout.setVerticalGroup(
            panelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        menuFile.setText(bundle.getString("menuFile")); // NOI18N
        menuFile.setLabel(bundle.getString("menuFile")); // NOI18N

        menuExport.setText(bundle.getString("menuFileExport")); // NOI18N
        menuFile.add(menuExport);

        menu.add(menuFile);

        menuMap.setText(bundle.getString("menuMap")); // NOI18N
        menuMap.setActionCommand("Settings");

        menuImportMap.setText(bundle.getString("menuMapImport")); // NOI18N
        menuMap.add(menuImportMap);
        menuMap.add(separator);

        menu.add(menuMap);

        setJMenuBar(menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void componentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_componentResized
        if (mapView != null) {
            mapView.setSize(panelMap.getWidth(), panelMap.getHeight());
        }
    }//GEN-LAST:event_componentResized

    private void sliderZoomStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderZoomStateChanged
        controller.setZoom(sliderZoom.getValue());
    }//GEN-LAST:event_sliderZoomStateChanged

    private void buttonPlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPlanActionPerformed
        controller.planTrip(fieldDelka.getText(), (RoutingTypes) comboRouting.getSelectedItem(), (TripTypes) comboTripType.getSelectedItem(), radioAll.isSelected(),
                checkMicro.isSelected(), checkSmall.isSelected(), checkRegular.isSelected(), checkLarge.isSelected(), checkOther.isSelected(),
                sliderDifficulty.getLowValue(), sliderDifficulty.getHighValue(), sliderTerrain.getLowValue(), sliderTerrain.getHighValue(),
                radioContainerIgnore.isSelected(), radioContainerSmall.isSelected(), radioContainerLarge.isSelected(),
                radioDifficultyIgnore.isSelected(), radioDifficultySmall.isSelected(), radioDifficultyGreat.isSelected(),
                radioTerrainIgnore.isSelected(), radioTerrainLight.isSelected(), radioTerrainDifficult.isSelected());
    }//GEN-LAST:event_buttonPlanActionPerformed

    private void buttonZoomPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonZoomPlusActionPerformed
        controller.zoomIn();
    }//GEN-LAST:event_buttonZoomPlusActionPerformed

    private void buttonZoomMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonZoomMinusActionPerformed
        controller.zoomOut();
    }//GEN-LAST:event_buttonZoomMinusActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonLink;
    private javax.swing.JButton buttonPlan;
    private javax.swing.JButton buttonZoomMinus;
    private javax.swing.JButton buttonZoomPlus;
    private javax.swing.JCheckBox checkLarge;
    private javax.swing.JCheckBox checkMicro;
    private javax.swing.JCheckBox checkOther;
    private javax.swing.JCheckBox checkRegular;
    private javax.swing.JCheckBox checkSmall;
    private javax.swing.JComboBox comboRouting;
    private javax.swing.JComboBox comboTripType;
    private javax.swing.JTextField fieldDelka;
    private javax.swing.ButtonGroup groupContainer;
    private javax.swing.ButtonGroup groupDifficulty;
    private javax.swing.ButtonGroup groupState;
    private javax.swing.ButtonGroup groupTerrain;
    private javax.swing.JLabel labelContainer;
    private javax.swing.JLabel labelDifficulty;
    private javax.swing.JLabel labelKm;
    private javax.swing.JLabel labelLength;
    private javax.swing.JLabel labelRouting;
    private javax.swing.JLabel labelTerrain;
    private javax.swing.JLabel labelTripCaches;
    private javax.swing.JLabel labelTripKm;
    private javax.swing.JLabel labelTripLength;
    private javax.swing.JLabel labelTripTime;
    private javax.swing.JLabel labelTripType;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenuItem menuExport;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuImportMap;
    private javax.swing.JMenu menuMap;
    private javax.swing.JPanel panelCacheInfo;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelFilterContainer;
    private javax.swing.JPanel panelFilterDifficulty;
    private javax.swing.JPanel panelFilterState;
    private javax.swing.JPanel panelFilterTerrain;
    private javax.swing.JPanel panelLeft;
    private javax.swing.JPanel panelMap;
    private javax.swing.JPanel panelPreferences;
    private javax.swing.JPanel panelPreferencesContainer;
    private javax.swing.JPanel panelPreferencesDifficulty;
    private javax.swing.JPanel panelPreferencesTerrain;
    private javax.swing.JPanel panelRight;
    private javax.swing.JPanel panelTrip;
    private javax.swing.JPanel panelTripInfo;
    private javax.swing.JRadioButton radioAll;
    private javax.swing.JRadioButton radioContainerIgnore;
    private javax.swing.JRadioButton radioContainerLarge;
    private javax.swing.JRadioButton radioContainerSmall;
    private javax.swing.JRadioButton radioDifficultyGreat;
    private javax.swing.JRadioButton radioDifficultyIgnore;
    private javax.swing.JRadioButton radioDifficultySmall;
    private javax.swing.JRadioButton radioNotFound;
    private javax.swing.JRadioButton radioTerrainDifficult;
    private javax.swing.JRadioButton radioTerrainIgnore;
    private javax.swing.JRadioButton radioTerrainLight;
    private javax.swing.JPopupMenu.Separator separator;
    private javax.swing.JSlider sliderZoom;
    private javax.swing.JTextField textContainer;
    private javax.swing.JTextField textCoordinates;
    private javax.swing.JTextField textDifficulty;
    private javax.swing.JTextField textName;
    private javax.swing.JTextField textTerrain;
    private javax.swing.JTextField textTripCaches;
    private javax.swing.JTextField textTripLength;
    private javax.swing.JTextField textTripTime;
    // End of variables declaration//GEN-END:variables
}
