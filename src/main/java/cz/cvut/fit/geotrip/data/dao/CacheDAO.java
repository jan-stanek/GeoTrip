package cz.cvut.fit.geotrip.data.dao;

import com.google.common.base.Predicate;
import cz.cvut.fit.geotrip.data.GeoCache;
import java.util.Collection;

/**
 *
 * @author jan
 */
public interface CacheDAO {
    
    Collection<GeoCache> getAll();
    Collection<GeoCache> getFiltered(Predicate<GeoCache> predicate);
}
