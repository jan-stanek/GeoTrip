/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.business.routing;

import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import cz.cvut.fit.geotrip.data.entities.GeoPoint;
import java.util.List;

/**
 *
 * @author jan
 */
public interface Router {

    public void init(String mapName, String vehicle, GeoPlace ref, List<GeoCache> caches);
    public double[][] getDistanceMatrix() ;
    public long[][] getTimeMatrix();
    public List<GeoPoint>[][] getRouteMatrix();
    public void importMap(String mapName);
}
