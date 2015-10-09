/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.AlgorithmOptions;
import com.jidesoft.swing.RangeSlider;
import cz.cvut.fit.geotrip.geopoint.Cache;
import cz.cvut.fit.geotrip.geopoint.CacheContainer;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import javafx.scene.AccessibleRole;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.multi.MultiSliderUI;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.FixedPixelCircle;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.reader.ReadBuffer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.scalebar.DistanceUnitAdapter;
import org.mapsforge.map.scalebar.MapScaleBar;
import org.mapsforge.map.swing.controller.MapViewComponentListener;
import org.mapsforge.map.swing.controller.MouseEventListener;
import org.mapsforge.map.swing.view.MapView;
import org.mapsforge.map.util.MapViewProjection;

/**
 *
 * @author jan
 */
public class MainFrame extends javax.swing.JFrame {

    GeoTrip geotrip;
    public static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;

    public MapView mapView;
    public Model mapViewModel;
    public BoundingBox boundingBox;

    private final byte ZOOM_MIN = 6;
    private final byte ZOOM_MAX = 20;
    private final byte ZOOM_DEFAULT = 13;

    private RangeSlider sliderObtiznost;
    private RangeSlider sliderTeren;

    /**
     * Creates MainFrame
     */
    public MainFrame(GeoTrip geotrip) {
        this.geotrip = geotrip;

        initComponents();

        addMapView();
        addRangeSliders();
    }

    private void addMapView() {
        panelPravy.setBorder(new CompoundBorder(new EmptyBorder(6, 2, 2, 2), panelPravy.getBorder()));

        createMapView();
        mapView.setSize(panelMapa.getWidth(), panelMapa.getHeight());
        panelMapa.add(mapView);

        mapViewModel.mapViewPosition.setZoomLevelMin(ZOOM_MIN);
        mapViewModel.mapViewPosition.setZoomLevelMax(ZOOM_MAX);

        sliderZoom.setMinimum(ZOOM_MIN);
        sliderZoom.setMaximum(ZOOM_MAX);
    }

    private void addRangeSliders() {
        Hashtable labelTable = new Hashtable();
        labelTable.put(1, new JLabel("1"));
        labelTable.put(3, new JLabel("2"));
        labelTable.put(5, new JLabel("3"));
        labelTable.put(7, new JLabel("4"));
        labelTable.put(9, new JLabel("5"));

        sliderObtiznost = new RangeSlider(1, 9, 1, 9);
        sliderObtiznost.setPaintTicks(true);
        sliderObtiznost.setPaintLabels(true);
        sliderObtiznost.setMajorTickSpacing(2);
        sliderObtiznost.setMinorTickSpacing(1);
        sliderObtiznost.setSize(267, 40);
        sliderObtiznost.setLocation(6, 16);
        sliderObtiznost.setLabelTable(labelTable);
        panelFiltrObtiznost.add(sliderObtiznost);

        sliderTeren = new RangeSlider(1, 9, 1, 9);
        sliderTeren.setPaintTicks(true);
        sliderTeren.setPaintLabels(true);
        sliderTeren.setMajorTickSpacing(2);
        sliderTeren.setMinorTickSpacing(1);
        sliderTeren.setSize(267, 40);
        sliderTeren.setLocation(6, 16);
        sliderTeren.setLabelTable(labelTable);
        panelFiltrTeren.add(sliderTeren);
    }

    private void createMapView() {
        mapView = new MapView();
        mapViewModel = mapView.getModel();
        mapView.getMapScaleBar().setVisible(true);
       
        mapView.addComponentListener(new MapViewComponentListener(mapView, mapViewModel.mapViewDimension));

        MouseEventListener mouseEventListener = new MouseEventListener(mapViewModel);
        mapView.addMouseMotionListener(mouseEventListener);

        mapView.addMouseListener(new java.awt.event.MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Layers layers = geotrip.getLayers();
                MapViewProjection mapViewProjection = new MapViewProjection(mapView);
                
                for (int i = layers.size() - 1; i >= 0; --i) {
                    Layer layer = layers.get(i);
                    Point layerPosition = mapViewProjection.toPixels(layer.getPosition());
                    Point clickPosition = new Point(e.getX(), e.getY());
                    if (layer.onTap(layer.getPosition(), layerPosition, clickPosition)) {
                        geotrip.showInfo(layer);
                        break;
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });

        mapView.addMouseListener(mouseEventListener);

        mapView.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            @Override
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                byte zoom = (byte) (mapViewModel.mapViewPosition.getZoomLevel() - evt.getWheelRotation());
                mapViewModel.mapViewPosition.setZoomLevel(zoom);
                sliderZoom.setValue(zoom);
            }
        });

        mapView.addMouseListener(mouseEventListener);
    }

    public void zoomToRef() {
        zoomTo(ZOOM_DEFAULT);
        mapViewModel.mapViewPosition.setCenter(geotrip.ref.coordinates);
    }

    private void zoomTo(byte zoom) {
        mapViewModel.mapViewPosition.setZoomLevel(zoom);
        sliderZoom.setValue(zoom);
    }

    private void filter() {
        int container = 0;
        if (checkMikro.isSelected()) {
            container |= CacheContainer.MICRO.getValue();
        }
        if (checkMala.isSelected()) {
            container |= CacheContainer.SMALL.getValue();
        }
        if (checkStredni.isSelected()) {
            container |= CacheContainer.REGULAR.getValue();
        }
        if (checkVelka.isSelected()) {
            container |= CacheContainer.LARGE.getValue();
        }
        if (checkOstatni.isSelected()) {
            container |= CacheContainer.OTHER.getValue();
        }

        geotrip.filter(radioVsechny.isSelected(), container, sliderObtiznost.getLowValue(), sliderObtiznost.getHighValue(), sliderTeren.getLowValue(), sliderTeren.getHighValue());
    }
    
    public void showInfo(String name) {
        
    }
    
    public void hideInfo() {
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupStav = new javax.swing.ButtonGroup();
        groupVelikost = new javax.swing.ButtonGroup();
        groupObtiznost = new javax.swing.ButtonGroup();
        groupTeren = new javax.swing.ButtonGroup();
        panelLevy = new javax.swing.JPanel();
        panelFiltr = new javax.swing.JPanel();
        panelFiltrStav = new javax.swing.JPanel();
        radioVsechny = new javax.swing.JRadioButton();
        radioNenalezene = new javax.swing.JRadioButton();
        panelFiltrVelikost = new javax.swing.JPanel();
        checkMikro = new javax.swing.JCheckBox();
        checkMala = new javax.swing.JCheckBox();
        checkStredni = new javax.swing.JCheckBox();
        checkVelka = new javax.swing.JCheckBox();
        checkOstatni = new javax.swing.JCheckBox();
        panelFiltrObtiznost = new javax.swing.JPanel();
        panelFiltrTeren = new javax.swing.JPanel();
        panelPreference = new javax.swing.JPanel();
        panelPreferenceVelikost = new javax.swing.JPanel();
        radioVelikostMala = new javax.swing.JRadioButton();
        radioVelikostVelka = new javax.swing.JRadioButton();
        radioVelikostNezalezi = new javax.swing.JRadioButton();
        panelPreferenceTeren = new javax.swing.JPanel();
        radioTerenLehky = new javax.swing.JRadioButton();
        radioTerenNarocny = new javax.swing.JRadioButton();
        radioTerenNezalezi = new javax.swing.JRadioButton();
        panelPreferenceObtiznost = new javax.swing.JPanel();
        radioObtiznostMala = new javax.swing.JRadioButton();
        radioObtiznostVelka = new javax.swing.JRadioButton();
        radioObtiznostNezalezi = new javax.swing.JRadioButton();
        buttonNaplanovat = new javax.swing.JButton();
        panelVylet = new javax.swing.JPanel();
        labelDelka = new javax.swing.JLabel();
        fieldDelka = new javax.swing.JTextField();
        labelTrasovani = new javax.swing.JLabel();
        comboTrasovani = new javax.swing.JComboBox();
        labelKm = new javax.swing.JLabel();
        panelPravy = new javax.swing.JPanel();
        panelMapa = new javax.swing.JPanel();
        sliderZoom = new javax.swing.JSlider();
        buttonZoomPlus = new javax.swing.JButton();
        buttonZoomMinus = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 560));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                MainFrame.this.componentResized(evt);
            }
        });

        panelFiltr.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtr"));

        panelFiltrStav.setBorder(javax.swing.BorderFactory.createTitledBorder("Stav"));

        groupStav.add(radioVsechny);
        radioVsechny.setSelected(true);
        radioVsechny.setText("všechny");

        groupStav.add(radioNenalezene);
        radioNenalezene.setText("nenalezené");

        javax.swing.GroupLayout panelFiltrStavLayout = new javax.swing.GroupLayout(panelFiltrStav);
        panelFiltrStav.setLayout(panelFiltrStavLayout);
        panelFiltrStavLayout.setHorizontalGroup(
            panelFiltrStavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrStavLayout.createSequentialGroup()
                .addComponent(radioVsechny)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioNenalezene)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFiltrStavLayout.setVerticalGroup(
            panelFiltrStavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrStavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(radioVsechny)
                .addComponent(radioNenalezene))
        );

        panelFiltrVelikost.setBorder(javax.swing.BorderFactory.createTitledBorder("Velikost"));

        checkMikro.setSelected(true);
        checkMikro.setText("mikro");

        checkMala.setSelected(true);
        checkMala.setText("malá");

        checkStredni.setSelected(true);
        checkStredni.setText("střední");

        checkVelka.setSelected(true);
        checkVelka.setText("velká");

        checkOstatni.setSelected(true);
        checkOstatni.setText("ostatní");

        javax.swing.GroupLayout panelFiltrVelikostLayout = new javax.swing.GroupLayout(panelFiltrVelikost);
        panelFiltrVelikost.setLayout(panelFiltrVelikostLayout);
        panelFiltrVelikostLayout.setHorizontalGroup(
            panelFiltrVelikostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrVelikostLayout.createSequentialGroup()
                .addComponent(checkMikro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkMala)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkStredni)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkVelka)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkOstatni))
        );
        panelFiltrVelikostLayout.setVerticalGroup(
            panelFiltrVelikostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrVelikostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(checkMikro)
                .addComponent(checkMala)
                .addComponent(checkStredni)
                .addComponent(checkVelka)
                .addComponent(checkOstatni))
        );

        panelFiltrObtiznost.setBorder(javax.swing.BorderFactory.createTitledBorder("Obtížnost"));

        javax.swing.GroupLayout panelFiltrObtiznostLayout = new javax.swing.GroupLayout(panelFiltrObtiznost);
        panelFiltrObtiznost.setLayout(panelFiltrObtiznostLayout);
        panelFiltrObtiznostLayout.setHorizontalGroup(
            panelFiltrObtiznostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFiltrObtiznostLayout.setVerticalGroup(
            panelFiltrObtiznostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        panelFiltrTeren.setBorder(javax.swing.BorderFactory.createTitledBorder("Terén"));
        panelFiltrTeren.setPreferredSize(new java.awt.Dimension(286, 63));

        javax.swing.GroupLayout panelFiltrTerenLayout = new javax.swing.GroupLayout(panelFiltrTeren);
        panelFiltrTeren.setLayout(panelFiltrTerenLayout);
        panelFiltrTerenLayout.setHorizontalGroup(
            panelFiltrTerenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFiltrTerenLayout.setVerticalGroup(
            panelFiltrTerenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelFiltrLayout = new javax.swing.GroupLayout(panelFiltr);
        panelFiltr.setLayout(panelFiltrLayout);
        panelFiltrLayout.setHorizontalGroup(
            panelFiltrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFiltrTeren, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
            .addComponent(panelFiltrObtiznost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFiltrStav, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFiltrVelikost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelFiltrLayout.setVerticalGroup(
            panelFiltrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrLayout.createSequentialGroup()
                .addComponent(panelFiltrStav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFiltrVelikost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFiltrObtiznost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelFiltrTeren, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelPreference.setBorder(javax.swing.BorderFactory.createTitledBorder("Preference"));

        panelPreferenceVelikost.setBorder(javax.swing.BorderFactory.createTitledBorder("Velikost"));

        groupVelikost.add(radioVelikostMala);
        radioVelikostMala.setText("malá");

        groupVelikost.add(radioVelikostVelka);
        radioVelikostVelka.setText("velká");

        groupVelikost.add(radioVelikostNezalezi);
        radioVelikostNezalezi.setSelected(true);
        radioVelikostNezalezi.setText("nezáleží");

        javax.swing.GroupLayout panelPreferenceVelikostLayout = new javax.swing.GroupLayout(panelPreferenceVelikost);
        panelPreferenceVelikost.setLayout(panelPreferenceVelikostLayout);
        panelPreferenceVelikostLayout.setHorizontalGroup(
            panelPreferenceVelikostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferenceVelikostLayout.createSequentialGroup()
                .addComponent(radioVelikostNezalezi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioVelikostMala)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioVelikostVelka)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPreferenceVelikostLayout.setVerticalGroup(
            panelPreferenceVelikostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferenceVelikostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(radioVelikostMala)
                .addComponent(radioVelikostVelka)
                .addComponent(radioVelikostNezalezi))
        );

        panelPreferenceTeren.setBorder(javax.swing.BorderFactory.createTitledBorder("Terén"));

        groupTeren.add(radioTerenLehky);
        radioTerenLehky.setText("lehký");

        groupTeren.add(radioTerenNarocny);
        radioTerenNarocny.setText("náročný");

        groupTeren.add(radioTerenNezalezi);
        radioTerenNezalezi.setSelected(true);
        radioTerenNezalezi.setText("nezáleží");

        javax.swing.GroupLayout panelPreferenceTerenLayout = new javax.swing.GroupLayout(panelPreferenceTeren);
        panelPreferenceTeren.setLayout(panelPreferenceTerenLayout);
        panelPreferenceTerenLayout.setHorizontalGroup(
            panelPreferenceTerenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferenceTerenLayout.createSequentialGroup()
                .addComponent(radioTerenNezalezi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioTerenLehky)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioTerenNarocny)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPreferenceTerenLayout.setVerticalGroup(
            panelPreferenceTerenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferenceTerenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(radioTerenLehky)
                .addComponent(radioTerenNarocny)
                .addComponent(radioTerenNezalezi))
        );

        panelPreferenceObtiznost.setBorder(javax.swing.BorderFactory.createTitledBorder("Obtížnost"));

        groupObtiznost.add(radioObtiznostMala);
        radioObtiznostMala.setText("malá");

        groupObtiznost.add(radioObtiznostVelka);
        radioObtiznostVelka.setText("velká");

        groupObtiznost.add(radioObtiznostNezalezi);
        radioObtiznostNezalezi.setSelected(true);
        radioObtiznostNezalezi.setText("nezáleží");

        javax.swing.GroupLayout panelPreferenceObtiznostLayout = new javax.swing.GroupLayout(panelPreferenceObtiznost);
        panelPreferenceObtiznost.setLayout(panelPreferenceObtiznostLayout);
        panelPreferenceObtiznostLayout.setHorizontalGroup(
            panelPreferenceObtiznostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferenceObtiznostLayout.createSequentialGroup()
                .addComponent(radioObtiznostNezalezi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioObtiznostMala)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioObtiznostVelka)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPreferenceObtiznostLayout.setVerticalGroup(
            panelPreferenceObtiznostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferenceObtiznostLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(radioObtiznostMala)
                .addComponent(radioObtiznostVelka)
                .addComponent(radioObtiznostNezalezi))
        );

        javax.swing.GroupLayout panelPreferenceLayout = new javax.swing.GroupLayout(panelPreference);
        panelPreference.setLayout(panelPreferenceLayout);
        panelPreferenceLayout.setHorizontalGroup(
            panelPreferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPreferenceVelikost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelPreferenceObtiznost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelPreferenceTeren, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelPreferenceLayout.setVerticalGroup(
            panelPreferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreferenceLayout.createSequentialGroup()
                .addComponent(panelPreferenceVelikost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPreferenceObtiznost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPreferenceTeren, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        buttonNaplanovat.setText("Naplánovat výlet");
        buttonNaplanovat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNaplanovatActionPerformed(evt);
            }
        });

        panelVylet.setBorder(javax.swing.BorderFactory.createTitledBorder("Výlet"));

        labelDelka.setText("Maximální délka:");

        fieldDelka.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        labelTrasovani.setText("Trasa pro:");

        comboTrasovani.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "chůze", "kolo", "auto" }));

        labelKm.setText("km");

        javax.swing.GroupLayout panelVyletLayout = new javax.swing.GroupLayout(panelVylet);
        panelVylet.setLayout(panelVyletLayout);
        panelVyletLayout.setHorizontalGroup(
            panelVyletLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVyletLayout.createSequentialGroup()
                .addGroup(panelVyletLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDelka)
                    .addComponent(labelTrasovani))
                .addGap(4, 4, 4)
                .addGroup(panelVyletLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelVyletLayout.createSequentialGroup()
                        .addComponent(fieldDelka)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelKm))
                    .addComponent(comboTrasovani, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        panelVyletLayout.setVerticalGroup(
            panelVyletLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVyletLayout.createSequentialGroup()
                .addGroup(panelVyletLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldDelka, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDelka)
                    .addComponent(labelKm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelVyletLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboTrasovani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTrasovani)))
        );

        javax.swing.GroupLayout panelLevyLayout = new javax.swing.GroupLayout(panelLevy);
        panelLevy.setLayout(panelLevyLayout);
        panelLevyLayout.setHorizontalGroup(
            panelLevyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonNaplanovat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelVylet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFiltr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelPreference, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelLevyLayout.setVerticalGroup(
            panelLevyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLevyLayout.createSequentialGroup()
                .addComponent(panelVylet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFiltr, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPreference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonNaplanovat)
                .addContainerGap())
        );

        panelPravy.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        sliderZoom.setMajorTickSpacing(1);
        sliderZoom.setMaximum(20);
        sliderZoom.setOrientation(javax.swing.JSlider.VERTICAL);
        sliderZoom.setSnapToTicks(true);
        sliderZoom.setValue(0);
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
        buttonZoomMinus.setPreferredSize(new java.awt.Dimension(20, 20));
        buttonZoomMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonZoomMinusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMapaLayout = new javax.swing.GroupLayout(panelMapa);
        panelMapa.setLayout(panelMapaLayout);
        panelMapaLayout.setHorizontalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonZoomPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonZoomMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderZoom, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(446, Short.MAX_VALUE))
        );
        panelMapaLayout.setVerticalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonZoomPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonZoomMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelPravyLayout = new javax.swing.GroupLayout(panelPravy);
        panelPravy.setLayout(panelPravyLayout);
        panelPravyLayout.setHorizontalGroup(
            panelPravyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMapa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelPravyLayout.setVerticalGroup(
            panelPravyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMapa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelLevy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPravy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelLevy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelPravy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void componentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_componentResized
        if (mapView != null) {
            mapView.setSize(panelMapa.getWidth(), panelMapa.getHeight());
        }
    }//GEN-LAST:event_componentResized

    private void sliderZoomStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderZoomStateChanged
        mapView.getModel().mapViewPosition.setZoomLevel((byte) sliderZoom.getValue());
    }//GEN-LAST:event_sliderZoomStateChanged

    private void buttonNaplanovatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNaplanovatActionPerformed
        filter();
    }//GEN-LAST:event_buttonNaplanovatActionPerformed

    private void buttonZoomPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonZoomPlusActionPerformed
        byte zoomLevel = mapView.getModel().mapViewPosition.getZoomLevel();
        zoomLevel++;
        sliderZoom.setValue(zoomLevel);
        mapView.getModel().mapViewPosition.setZoomLevel(zoomLevel);
    }//GEN-LAST:event_buttonZoomPlusActionPerformed

    private void buttonZoomMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonZoomMinusActionPerformed
        byte zoomLevel = mapView.getModel().mapViewPosition.getZoomLevel();
        zoomLevel--;
        sliderZoom.setValue(zoomLevel);
        mapView.getModel().mapViewPosition.setZoomLevel(zoomLevel);
    }//GEN-LAST:event_buttonZoomMinusActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonNaplanovat;
    private javax.swing.JButton buttonZoomMinus;
    private javax.swing.JButton buttonZoomPlus;
    private javax.swing.JCheckBox checkMala;
    private javax.swing.JCheckBox checkMikro;
    private javax.swing.JCheckBox checkOstatni;
    private javax.swing.JCheckBox checkStredni;
    private javax.swing.JCheckBox checkVelka;
    private javax.swing.JComboBox comboTrasovani;
    private javax.swing.JTextField fieldDelka;
    private javax.swing.ButtonGroup groupObtiznost;
    private javax.swing.ButtonGroup groupStav;
    private javax.swing.ButtonGroup groupTeren;
    private javax.swing.ButtonGroup groupVelikost;
    private javax.swing.JLabel labelDelka;
    private javax.swing.JLabel labelKm;
    private javax.swing.JLabel labelTrasovani;
    private javax.swing.JPanel panelFiltr;
    private javax.swing.JPanel panelFiltrObtiznost;
    private javax.swing.JPanel panelFiltrStav;
    private javax.swing.JPanel panelFiltrTeren;
    private javax.swing.JPanel panelFiltrVelikost;
    private javax.swing.JPanel panelLevy;
    private javax.swing.JPanel panelMapa;
    private javax.swing.JPanel panelPravy;
    private javax.swing.JPanel panelPreference;
    private javax.swing.JPanel panelPreferenceObtiznost;
    private javax.swing.JPanel panelPreferenceTeren;
    private javax.swing.JPanel panelPreferenceVelikost;
    private javax.swing.JPanel panelVylet;
    private javax.swing.JRadioButton radioNenalezene;
    private javax.swing.JRadioButton radioObtiznostMala;
    private javax.swing.JRadioButton radioObtiznostNezalezi;
    private javax.swing.JRadioButton radioObtiznostVelka;
    private javax.swing.JRadioButton radioTerenLehky;
    private javax.swing.JRadioButton radioTerenNarocny;
    private javax.swing.JRadioButton radioTerenNezalezi;
    private javax.swing.JRadioButton radioVelikostMala;
    private javax.swing.JRadioButton radioVelikostNezalezi;
    private javax.swing.JRadioButton radioVelikostVelka;
    private javax.swing.JRadioButton radioVsechny;
    private javax.swing.JSlider sliderZoom;
    // End of variables declaration//GEN-END:variables
}
