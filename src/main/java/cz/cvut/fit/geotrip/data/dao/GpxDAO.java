package cz.cvut.fit.geotrip.data.dao;

import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import java.util.Collection;

public interface GpxDAO {

    /**
     * Returns ref point.
     * 
     * @return ref point
     */
    GeoPlace getRef();

    /**
     * Returns list of caches in GPX file.
     * 
     * @return list of caches in GPX file
     */
    Collection<GeoCache> getAllCaches();
}
