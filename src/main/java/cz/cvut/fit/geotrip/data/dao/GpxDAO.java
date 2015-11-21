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
