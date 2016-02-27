package cz.cvut.fit.geotrip.business.router;

import cz.cvut.fit.geotrip.business.RoutingTypes;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import cz.cvut.fit.geotrip.data.entities.GeoPoint;
import java.util.Collection;
import java.util.List;

public interface Router {

    public void init(String mapName, RoutingTypes vehicle, GeoPlace ref, Collection<GeoCache> caches);

    public double[][] getDistanceMatrix();

    public long[][] getTimeMatrix();

    public List<GeoPoint>[][] getRouteMatrix();

    public void importMap(String mapName);
}
