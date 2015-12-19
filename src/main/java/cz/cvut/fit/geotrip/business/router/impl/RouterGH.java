package cz.cvut.fit.geotrip.business.router.impl;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import cz.cvut.fit.geotrip.GeoTrip;
import cz.cvut.fit.geotrip.business.MainModel;
import cz.cvut.fit.geotrip.business.router.Router;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import cz.cvut.fit.geotrip.data.entities.GeoPoint;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jan
 */
public class RouterGH implements Router {

    private final String MAPS_DIRECTORY = "maps/";
    private final String GH_DIRECTORY = "gh/";
    private final String OSM_EXTENSION = ".osm.pbf";
    
    private GraphHopper gh;
    private String vehicle;
    private double[][] distanceMatrix;
    private long[][] timeMatrix;
    private List<GeoPoint>[][] routeMatrix;
   
    
    @Override
    public void init(String mapName, String vehicle, GeoPlace ref, List<GeoCache> caches) {
        gh = new GraphHopper().setEncodingManager(new EncodingManager(vehicle)).forDesktop();
        gh.load(GeoTrip.DATA_DIRECTORY + GH_DIRECTORY + mapName + "/" + vehicle);
        this.vehicle = vehicle;
        countMatrix(ref, caches);
    }
    
    @Override
    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    @Override
    public long[][] getTimeMatrix() {
        return timeMatrix;
    }
    
    @Override
    public List<GeoPoint>[][] getRouteMatrix() {
        return routeMatrix;
    }
    
    @Override
    public void importMap(String mapName) {
        Thread car = initMap(mapName, "car"); 
        Thread bike = initMap(mapName, "bike");
        Thread foot = initMap(mapName, "foot");
        
        car.start();
        bike.start();
        foot.start();
        
        try {
            car.join();
            bike.join();
            foot.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void countMatrix(GeoPlace ref, List<GeoCache> caches) {
        List<GeoPoint> nodes = new ArrayList<>();
        nodes.add(ref.getCoordinates());
        for (GeoCache c : caches)
            nodes.add(c.getCoordinates());
        
        int n = nodes.size();
        
        distanceMatrix = new double[n][n];
        timeMatrix = new long[n][n];
        routeMatrix = new LinkedList[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                    continue;
                }
                
                GeoPoint from = nodes.get(i);
                GeoPoint to = nodes.get(j);
                
                GHRequest req = new GHRequest(from.getLat(), from.getLon(), to.getLat(), to.getLon());
                req.getHints().put("instruction", false);
                req.setAlgorithm(AlgorithmOptions.ASTAR_BI);
                req.setVehicle(vehicle);
                
                GHResponse res = gh.route(req);
                distanceMatrix[i][j] = countDistance(from, to, res);
                routeMatrix[i][j] = createRoute(from, to, res);
                timeMatrix[i][j] = (long) (res.getTime() * (distanceMatrix[i][j] / res.getDistance()));
            }
        }
    }
    
    private List<GeoPoint> createRoute(GeoPoint from, GeoPoint to, GHResponse res) {
        PointList pl = res.getPoints();
        
        List<GeoPoint> route = new LinkedList<>();
        route.add(from);
        
        for (int i = 0; i < pl.size(); i++)
            route.add(new GeoPoint(pl.getLat(i), pl.getLon(i)));
        
        route.add(to);
        
        return route;
    }
    
    private double countDistance(GeoPoint from, GeoPoint to, GHResponse res) {
        PointList pl = res.getPoints();
        int plSize = pl.size();
        
        double b = from.getCircleDistance(pl.getLat(0), pl.getLon(0));
        double e = to.getCircleDistance(pl.getLat(plSize-1), pl.getLon(plSize-1));
        return res.getDistance() + b + e;
    }
    
    private Thread initMap(final String mapName, final String vehicle) {
        return new Thread() {
            @Override
            public void run() {
                gh = new GraphHopper().setGraphHopperLocation(GeoTrip.DATA_DIRECTORY + GH_DIRECTORY + mapName + "/" + vehicle).setEncodingManager(new EncodingManager(vehicle)).setOSMFile(new File(GeoTrip.DATA_DIRECTORY + MAPS_DIRECTORY + mapName + OSM_EXTENSION).getAbsolutePath()).forDesktop();
                gh.importOrLoad();
            }
        };
    }
}
