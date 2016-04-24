package cz.cvut.fit.geotrip.business.router;

import cz.cvut.fit.geotrip.business.RoutingTypes;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import cz.cvut.fit.geotrip.data.entities.GeoPoint;
import java.util.Collection;
import java.util.List;

public interface Router {

    /**
     * Sets router parameters.
     * 
     * @param mapName name of selected map
     * @param vehicle vehicle
     * @param ref ref point
     * @param caches list of caches
     */
    public void init(String mapName, RoutingTypes vehicle, GeoPlace ref, Collection<GeoCache> caches);

    /**
     * Returns matrix of distances between caches.
     * 
     * @return matrix of distances between caches
     */
    public double[][] getDistanceMatrix();

    /**
     * Returns matrix of times between caches.
     * 
     * @return matrix of times between caches
     */
    public long[][] getTimeMatrix();

    /**
     * Returns matrix of found routes.
     * 
     * @return matrix of found routes
     */
    public List<GeoPoint>[][] getRouteMatrix();

    /**
     * Imports new map file.
     * 
     * @param mapName name of map file
     */
    public void importMap(String mapName);
}
