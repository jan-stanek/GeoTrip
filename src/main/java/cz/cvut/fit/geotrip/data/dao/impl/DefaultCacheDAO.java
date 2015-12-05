package cz.cvut.fit.geotrip.data.dao.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import cz.cvut.fit.geotrip.data.dao.CacheDAO;
import cz.cvut.fit.geotrip.data.dao.DAOFactory;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jan
 */
public class DefaultCacheDAO implements CacheDAO {

    public static CacheDAO instance = new DefaultCacheDAO();

    private final List<GeoCache> data;

    private DefaultCacheDAO() {
        data = new LinkedList<>();
        data.addAll(DAOFactory.getDAOFactory().getGpxDAO().getAllCaches());
    }

    @Override
    public Collection<GeoCache> getAll() {
        return new LinkedList<>(data);
    }
    
    @Override
    public Collection<GeoCache> getFiltered(Predicate<GeoCache> predicate) {
        return Lists.newLinkedList(Iterables.filter(data, predicate));
    }    
}
