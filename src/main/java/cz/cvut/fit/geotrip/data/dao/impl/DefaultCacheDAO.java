/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.data.dao.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import cz.cvut.fit.geotrip.data.dao.CacheDAO;
import cz.cvut.fit.geotrip.data.dao.DAOFactory;
import cz.cvut.fit.geotrip.data.GeoCache;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.mapsforge.map.layer.Layer;

/**
 *
 * @author jan
 */
public class DefaultCacheDAO implements CacheDAO {

    public static DefaultCacheDAO instance = new DefaultCacheDAO();

    private List<GeoCache> data;

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
