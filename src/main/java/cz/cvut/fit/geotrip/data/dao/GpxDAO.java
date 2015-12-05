package cz.cvut.fit.geotrip.data.dao;

import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import java.util.Collection;

/**
 *
 * @author jan
 */
public interface GpxDAO {
    
    GeoPlace getRef();
    Collection<GeoCache> getAllCaches();
}
