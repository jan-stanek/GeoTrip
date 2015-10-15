/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip;

import com.graphhopper.util.PointList;
import com.jidesoft.swing.RangeSlider;
import cz.cvut.fit.geotrip.geopoint.CacheContainer;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.swing.controller.MapViewComponentListener;
import org.mapsforge.map.swing.controller.MouseEventListener;
import org.mapsforge.map.swing.view.MapView;
import org.mapsforge.map.util.MapViewProjection;

/**
 *
 * @author jan
 */
public class MainFrame extends javax.swing.JFrame {

    private final GeoTrip geotrip;
    private static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;

    private MapView mapView;
    private Model mapViewModel;
    
    private final byte ZOOM_MIN = 6;
    private final byte ZOOM_MAX = 20;
    private final byte ZOOM_DEFAULT = 13;

    private RangeSlider sliderObtiznost;
    private RangeSlider sliderTeren;

    private Layers layers;
    private Layer mapLayer = null;
    private List<Layer> cacheLayers;
    private Layer routeLayer = null;
    private Bitmap iconFound, iconNotFound, iconRef;

    
    public MainFrame(GeoTrip geotrip) {
        this.geotrip = geotrip;
        
        initComponents();
        
        setIcon(getClass().getClassLoader().getResource("icon.png"));
                
        hideCacheInfo();
        
        addMapView();
        
        addRangeSliders();
        
        loadMarkersIcons();
        
        cacheLayers = new LinkedList<>();
    }
    
    private void setIcon(URL img) {
        ImageIcon icon = new ImageIcon(img);
        this.setIconImage(icon.getImage());
    }
    
    private void addMapView() {
        panelRight.setBorder(new CompoundBorder(new EmptyBorder(6, 2, 2, 2), panelRight.getBorder()));

        createMapView();
        mapView.setSize(panelMap.getWidth(), panelMap.getHeight());
        panelMap.add(mapView);

        mapViewModel.mapViewPosition.setZoomLevelMin(ZOOM_MIN);
        mapViewModel.mapViewPosition.setZoomLevelMax(ZOOM_MAX);

        sliderZoom.setMinimum(ZOOM_MIN);
        sliderZoom.setMaximum(ZOOM_MAX);
        sliderZoom.setValue(ZOOM_DEFAULT);
    }

    private void createMapView() {
        mapView = new MapView();
        mapViewModel = mapView.getModel();
        mapView.getMapScaleBar().setVisible(true);
        
        layers = mapView.getLayerManager().getLayers();
       
        mapView.addComponentListener(new MapViewComponentListener(mapView, mapViewModel.mapViewDimension));

        MouseEventListener mouseEventListener = new MouseEventListener(mapViewModel);
        mapView.addMouseMotionListener(mouseEventListener);
        mapView.addMouseListener(new java.awt.event.MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideCacheInfo();
                
                MapViewProjection mapViewProjection = new MapViewProjection(mapView);
                
                for (int i = layers.size() - 1; i >= 0; --i) {
                    Layer layer = layers.get(i);
                    Point layerPosition = mapViewProjection.toPixels(layer.getPosition());
                    Point clickPosition = new Point(e.getX(), e.getY());
                    if (layer.onTap(layer.getPosition(), layerPosition, clickPosition)) {
                        geotrip.showCacheInfo(layer);
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
                sliderZoom.setValue(zoom);
            }
        });
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
        sliderObtiznost.setFocusable(false);
        panelFilterDifficulty.add(sliderObtiznost);

        sliderTeren = new RangeSlider(1, 9, 1, 9);
        sliderTeren.setPaintTicks(true);
        sliderTeren.setPaintLabels(true);
        sliderTeren.setMajorTickSpacing(2);
        sliderTeren.setMinorTickSpacing(1);
        sliderTeren.setSize(267, 40);
        sliderTeren.setLocation(6, 16);
        sliderTeren.setLabelTable(labelTable);
        sliderTeren.setFocusable(false);
        panelFilterTerrain.add(sliderTeren);
    }
 
    public void moveTo(LatLong coordinates) {
        mapViewModel.mapViewPosition.setCenter(coordinates);
    }
    
    public void zoomTo(BoundingBox boundingBox) {
        moveTo(boundingBox.getCenterPoint());
        byte zoomLevel = LatLongUtils.zoomForBounds(mapViewModel.mapViewDimension.getDimension(), boundingBox, mapViewModel.displayModel.getTileSize());
        sliderZoom.setValue(zoomLevel);
    }

    public void showCacheInfo(String name, String coordinates, CacheContainer container, int difficulty, int terrain, String id, final String link) {
        textName.setText(name);
        textName.setCaretPosition(0);
        
        textCoordinates.setText(coordinates);
        textContainer.setText(container.getName());
        textDifficulty.setText(new DecimalFormat("#.#").format((difficulty + 1) / 2.0));
        textTerrain.setText(new DecimalFormat("#.#").format((terrain + 1) / 2.0));
        
        buttonLink.setText("<html><a href=\"\">" + id + "</a></html>");
        for (ActionListener al : buttonLink.getActionListeners())
            buttonLink.removeActionListener(al);
        buttonLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUrl(link);
            }
        });
        
        panelInfo.setVisible(true);
    }
    
    private void hideCacheInfo() {
        panelInfo.setVisible(false);
    }
    
    private void openUrl(String link) {
        try {
            URL url = new URL(link);
            URI uri = url.toURI();
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
                desktop.browse(uri);
            else
                JOptionPane.showMessageDialog(null, "Odkaz se nepodařilo otevřít.", "Chyba prohlížeče", ERROR);
        } catch (URISyntaxException | IOException ex) {
            JOptionPane.showMessageDialog(null, "Odkaz se nepodařilo otevřít.", "Chyba", ERROR);
        }
    }

    public void loadMap(File mapFile) {
        if (mapLayer != null)
            layers.remove(mapLayer);
        
        TileRendererLayer tileRendererLayer = createTileRendererLayer(createTileCache(0), mapViewModel.mapViewPosition, true, true, mapFile);
        mapLayer = tileRendererLayer;
        layers.add(0, mapLayer);
    }
    
    private TileCache createTileCache(int index) {
        TileCache firstLevelTileCache = new InMemoryTileCache(128);
        File cacheDirectory = new File(System.getProperty("java.io.tmpdir"), "mapsforge" + index);
        TileCache secondLevelTileCache = new FileSystemTileCache(1024, cacheDirectory, GRAPHIC_FACTORY);
        return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
    }

    private TileRendererLayer createTileRendererLayer(TileCache tileCache, MapViewPosition mapViewPosition, boolean isTransparent, boolean renderLabels, File mapFile) {
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, new MapFile(mapFile), mapViewPosition, isTransparent, renderLabels, GRAPHIC_FACTORY);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        return tileRendererLayer;
    }
    
    public void addRefMarker(LatLong coordinates) {
        Marker marker = new Marker(coordinates, iconRef, 0, -iconRef.getHeight()/2);
        layers.add(marker);
    }
        
    public Marker addCacheMarker(LatLong coordinates, boolean found) {
        Marker marker = new Marker(coordinates, found ? iconFound : iconNotFound, 0, -iconRef.getHeight()/2) {
            @Override
            public boolean onTap(LatLong cacheLatLong, Point cachePosition, Point clickPosition) {
                double dX = cachePosition.x - clickPosition.x;
                double dY = cachePosition.y - clickPosition.y;
                return dY < getBitmap().getHeight() && dY > 3 && Math.abs(dX) <= getBitmap().getWidth()/2;
            }
        };
        layers.add(marker);
        cacheLayers.add(marker);
        return marker;
    }
    
    public void removeCacheMarkers() {
        for (Layer layer : cacheLayers)
            layers.remove(layer);
        hideCacheInfo();
    }
    
    public void addRoute(PointList pl) {
        Paint paint = GRAPHIC_FACTORY.createPaint();
        paint.setColor(org.mapsforge.core.graphics.Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Style.STROKE);
    
        Polyline polyline = new Polyline(paint, GRAPHIC_FACTORY);
        
        for (int i = 0; i < pl.size(); i++)
            polyline.getLatLongs().add(new LatLong(pl.getLat(i), pl.getLon(i)));
        
        layers.add(1, polyline);
        routeLayer = polyline;
    }

    public void removeRoute() {
        if (routeLayer != null)
            layers.remove(routeLayer);
        routeLayer = null;
    }
    
    private void loadMarkersIcons() {
        try (InputStream iconFoundIS = getClass().getClassLoader().getResourceAsStream("markers/found.png");
                InputStream iconNotFoundIS = getClass().getClassLoader().getResourceAsStream("markers/notfound.png");
                InputStream iconRefIS = getClass().getClassLoader().getResourceAsStream("markers/ref.png");) {
            iconFound = MainFrame.GRAPHIC_FACTORY.createResourceBitmap(iconFoundIS, 0);
            iconNotFound = MainFrame.GRAPHIC_FACTORY.createResourceBitmap(iconNotFoundIS, 0);
            iconRef = MainFrame.GRAPHIC_FACTORY.createResourceBitmap(iconRefIS, 0);
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
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
        panelInfo = new javax.swing.JPanel();
        textName = new javax.swing.JTextField();
        textCoordinates = new javax.swing.JTextField();
        labelContainer = new javax.swing.JLabel();
        textContainer = new javax.swing.JTextField();
        labelDifficulty = new javax.swing.JLabel();
        textDifficulty = new javax.swing.JTextField();
        labelTerrain = new javax.swing.JLabel();
        textTerrain = new javax.swing.JTextField();
        buttonLink = new javax.swing.JButton();
        menu = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuExport = new javax.swing.JMenuItem();
        menuSettings = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GeoTrip");
        setMinimumSize(new java.awt.Dimension(800, 560));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                MainFrame.this.componentResized(evt);
            }
        });

        panelTrip.setBorder(javax.swing.BorderFactory.createTitledBorder("Výlet"));

        labelLength.setText("Maximální délka:");

        fieldDelka.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fieldDelka.setText("0");

        labelRouting.setText("Trasa pro:");

        comboRouting.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "chůze", "kolo", "auto" }));

        labelKm.setText("km");

        javax.swing.GroupLayout panelTripLayout = new javax.swing.GroupLayout(panelTrip);
        panelTrip.setLayout(panelTripLayout);
        panelTripLayout.setHorizontalGroup(
            panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTripLayout.createSequentialGroup()
                .addGroup(panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelLength)
                    .addComponent(labelRouting))
                .addGap(4, 4, 4)
                .addGroup(panelTripLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTripLayout.createSequentialGroup()
                        .addComponent(fieldDelka)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelKm))
                    .addComponent(comboRouting, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                    .addComponent(labelRouting)))
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
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
        checkLarge.setText("velká");
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
            .addComponent(panelFilterTerrain, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
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
        radioContainerSmall.setText("malá");

        groupContainer.add(radioContainerLarge);
        radioContainerLarge.setText("velká");

        groupContainer.add(radioContainerIgnore);
        radioContainerIgnore.setSelected(true);
        radioContainerIgnore.setText("nezáleží");

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
        radioDifficultySmall.setText("malá");

        groupDifficulty.add(radioDifficultyGreat);
        radioDifficultyGreat.setText("velká");

        groupDifficulty.add(radioDifficultyIgnore);
        radioDifficultyIgnore.setSelected(true);
        radioDifficultyIgnore.setText("nezáleží");

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
        radioTerrainLight.setText("lehký");

        groupTerrain.add(radioTerrainDifficult);
        radioTerrainDifficult.setText("náročný");

        groupTerrain.add(radioTerrainIgnore);
        radioTerrainIgnore.setSelected(true);
        radioTerrainIgnore.setText("nezáleží");

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
                .addContainerGap())
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

        panelInfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        textName.setEditable(false);
        textName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        textName.setText("název");
        textName.setBorder(null);

        textCoordinates.setEditable(false);
        textCoordinates.setText("gps");
        textCoordinates.setBorder(null);

        labelContainer.setText(bundle.getString("infoContainer")); // NOI18N

        textContainer.setEditable(false);
        textContainer.setText("ostatní");
        textContainer.setBorder(null);

        labelDifficulty.setText(bundle.getString("infoDifficulty")); // NOI18N

        textDifficulty.setEditable(false);
        textDifficulty.setText("1,5");
        textDifficulty.setBorder(null);

        labelTerrain.setText(bundle.getString("infoTerrain")); // NOI18N

        textTerrain.setEditable(false);
        textTerrain.setText("1,5");
        textTerrain.setBorder(null);

        buttonLink.setBorder(null);
        buttonLink.setContentAreaFilled(false);
        buttonLink.setFocusable(false);
        buttonLink.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        buttonLink.setLabel("odkaz");

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoLayout.createSequentialGroup()
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
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelInfoLayout.createSequentialGroup()
                                .addComponent(textCoordinates, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonLink)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textCoordinates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonLink))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelContainer)
                    .addComponent(textContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDifficulty)
                    .addComponent(textDifficulty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTerrain)
                    .addComponent(textTerrain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addGroup(panelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonZoomPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonZoomMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sliderZoom, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 462, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMapLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelMapLayout.setVerticalGroup(
            panelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonZoomPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonZoomMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        menuExport.setLabel("export trasy");
        menuFile.add(menuExport);

        menu.add(menuFile);

        menuSettings.setActionCommand("Settings");
        menuSettings.setLabel(bundle.getString("menuSettigns")); // NOI18N
        menuSettings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuSettingsMouseClicked(evt);
            }
        });
        menu.add(menuSettings);

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
        mapView.getModel().mapViewPosition.setZoomLevel((byte) sliderZoom.getValue());
    }//GEN-LAST:event_sliderZoomStateChanged

    private void buttonPlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPlanActionPerformed
        int distance = Integer.parseInt(fieldDelka.getText());
        
        String vehicle = "";
        
        switch(comboRouting.getSelectedItem().toString()) {
            case "chůze":
                vehicle = "foot";
                break;
            case "kolo":
                vehicle = "bike";
                break;
            case "auto":
                vehicle = "car";
                break;
        }
                
        int container = 0;

        if (checkMicro.isSelected())
            container |= CacheContainer.MICRO.getValue();
        if (checkSmall.isSelected())
            container |= CacheContainer.SMALL.getValue();
        if (checkRegular.isSelected())
            container |= CacheContainer.REGULAR.getValue();
        if (checkLarge.isSelected())
            container |= CacheContainer.LARGE.getValue();
        if (checkOther.isSelected())
            container |= CacheContainer.OTHER.getValue();

        geotrip.filter(distance, vehicle, radioAll.isSelected(), container, sliderObtiznost.getLowValue(), sliderObtiznost.getHighValue(), sliderTeren.getLowValue(), sliderTeren.getHighValue());
    }//GEN-LAST:event_buttonPlanActionPerformed

    private void buttonZoomPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonZoomPlusActionPerformed
        byte zoomLevel = mapView.getModel().mapViewPosition.getZoomLevel();
        sliderZoom.setValue(zoomLevel + 1);
    }//GEN-LAST:event_buttonZoomPlusActionPerformed

    private void buttonZoomMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonZoomMinusActionPerformed
        byte zoomLevel = mapView.getModel().mapViewPosition.getZoomLevel();
        sliderZoom.setValue(zoomLevel - 1);
    }//GEN-LAST:event_buttonZoomMinusActionPerformed

    private void menuSettingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuSettingsMouseClicked
        SettingsDialog settingsDialog = new SettingsDialog(this, true);
        settingsDialog.setLocation(this.getLocation().x + this.getSize().width / 2 - settingsDialog.getSize().width / 2,
                this.getLocation().y + this.getSize().height / 2 - settingsDialog.getSize().height / 2);
        settingsDialog.setVisible(true);
        geotrip.loadSettings();
    }//GEN-LAST:event_menuSettingsMouseClicked

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
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenuItem menuExport;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuSettings;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelFilterContainer;
    private javax.swing.JPanel panelFilterDifficulty;
    private javax.swing.JPanel panelFilterState;
    private javax.swing.JPanel panelFilterTerrain;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelLeft;
    private javax.swing.JPanel panelMap;
    private javax.swing.JPanel panelPreferences;
    private javax.swing.JPanel panelPreferencesContainer;
    private javax.swing.JPanel panelPreferencesDifficulty;
    private javax.swing.JPanel panelPreferencesTerrain;
    private javax.swing.JPanel panelRight;
    private javax.swing.JPanel panelTrip;
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
    private javax.swing.JSlider sliderZoom;
    private javax.swing.JTextField textContainer;
    private javax.swing.JTextField textCoordinates;
    private javax.swing.JTextField textDifficulty;
    private javax.swing.JTextField textName;
    private javax.swing.JTextField textTerrain;
    // End of variables declaration//GEN-END:variables
}
