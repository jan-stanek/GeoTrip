package cz.cvut.fit.geotrip.data.dao;

import cz.cvut.fit.geotrip.data.dao.impl.DefaultCacheDAO;
import cz.cvut.fit.geotrip.data.dao.impl.DefaultGpxDAO;

/**
 *
 * @author jan
 */
public class DAOFactory {
    
    private static DAOFactory daoFactory;
    
    public static DAOFactory getDAOFactory() {
        if (daoFactory == null)
            daoFactory = new DAOFactory();
        return daoFactory;
    }
    
    public CacheDAO getCacheDAO() {
        return DefaultCacheDAO.instance;
    }
    
    public GpxDAO getGpxDAO() {
        return DefaultGpxDAO.instance;
    }
}
