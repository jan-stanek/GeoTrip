package cz.cvut.fit.geotrip.data.dao;

import com.google.common.base.Predicate;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import java.util.Collection;

public interface CacheDAO {

    Collection<GeoCache> getAll();

    Collection<GeoCache> getFiltered(Predicate<GeoCache> predicate);
}
