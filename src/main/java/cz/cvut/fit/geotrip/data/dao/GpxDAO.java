/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.data.dao;

import cz.cvut.fit.geotrip.data.GeoCache;
import cz.cvut.fit.geotrip.data.GeoPoint;
import java.util.Collection;

/**
 *
 * @author jan
 */
public interface GpxDAO {
    
    GeoPoint getRef();
    Collection<GeoCache> getAllCaches();
}
