package cz.cvut.fit.geotrip.data.dao;

import com.google.common.base.Predicate;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import java.util.Collection;

public interface CacheDAO {
    
    /**
     * Returns list of all caches.
     * 
     * @return list of all caches
     */
    Collection<GeoCache> getAll();

    /**
     * Returns filtered list of caches.
     * 
     * @param predicate filter
     * @return filtered list of caches
     */
    Collection<GeoCache> getFiltered(Predicate<GeoCache> predicate);
}
