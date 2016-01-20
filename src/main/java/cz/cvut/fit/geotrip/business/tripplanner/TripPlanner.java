package cz.cvut.fit.geotrip.business.tripplanner;

import cz.cvut.fit.geotrip.business.planner.Planner;
import cz.cvut.fit.geotrip.business.planner.impl.ExactPlanner;
import cz.cvut.fit.geotrip.business.planner.impl.FastPlanner;
import cz.cvut.fit.geotrip.business.router.Router;
import cz.cvut.fit.geotrip.business.router.impl.RouterGH;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import cz.cvut.fit.geotrip.data.entities.GeoPoint;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jan
 */
public class TripPlanner {
    
    final static int EXACT_MAX = 17;
    final static int MATRIX_MAX = 40;
   
    private final String mapName;
    private final String vehicle;
    private GeoPlace ref;
    private double maxLength;
    private double length;
    private int containerPriority;
    private int difficultyPriority;
    private int terrainPriority;

    private int nodes;
    private List<Integer> route;
    private List<GeoCache> caches;
    private RankedCacheList rankedCaches;
    private double[][] distanceMatrix;
    private long[][] timeMatrix;
    private List[][] routeMatrix;

    private final Router router;
    
    
    public TripPlanner(String mapName, String vehicle) {
        this.mapName = mapName;
        this.vehicle = vehicle;
        
        route = new LinkedList<>();
        router = new RouterGH();
    }
    
    public void plan(GeoPlace ref, List<GeoCache> caches, double maxLength, int containerPriority, int difficultyPriority, int terrainPriority) {
        this.ref = ref;
        this.caches = new LinkedList<>(caches);
        this.maxLength = maxLength;
        this.containerPriority = containerPriority;
        this.difficultyPriority = difficultyPriority;
        this.terrainPriority = terrainPriority;
        
        nodes = caches.size()+1;
        Planner planner;
        
        if (nodes > MATRIX_MAX) {
            distanceMatrix = new double[nodes][nodes];
            timeMatrix = new long[nodes][nodes];
            routeMatrix = new LinkedList[nodes][nodes];
            
            for (int i = 0; i < nodes-1; i++) {
                GeoCache tmp = caches.get(i);
                for (int j = 0; j < i; j++) 
                    distanceMatrix[i][j] = distanceMatrix[j][i] = tmp.getCircleDistance(caches.get(j));
            }
            
            removeTooDistantCaches();
            rankCaches();

            while (nodes > MATRIX_MAX) {
                planner = new FastPlanner();
                planner.plan(nodes, distanceMatrix);
                length = planner.getLength();

                if (length > maxLength)
                    removeCache(rankedCaches.getLast().getGeoCache());
                else
                    break;
            }
        }
        
        router.init(mapName, vehicle, ref, this.caches);
        distanceMatrix = router.getDistanceMatrix();
        timeMatrix = router.getTimeMatrix();
        routeMatrix = router.getRouteMatrix();
        removeTooDistantCaches();
        rankCaches();
        
        while (true) {
            if (nodes > EXACT_MAX)
                planner = new FastPlanner();
            else
                planner = new ExactPlanner();
            planner.plan(nodes, distanceMatrix);
            length = planner.getLength();
            
            if (length > maxLength)
                removeCache(rankedCaches.getLast().getGeoCache());
            else
                break;
        }
        route = planner.getRoute();
    }

    public double getTripLength() {
        return length;
    }
    
    public long getTripTime() {
        long time = 0;
        int i;
        for (i = 0; i < route.size()-2; i++) {
            time += getRouteTime(route.get(i), route.get(i+1));
            time += getCacheTime(route.get(i+1));
        }
        time += getRouteTime(route.get(i), route.get(i+1));
        return time;
    }
    
    public int getTripCaches() {
        return caches.size();
    }

    public List<GeoPoint> getTripPoints() {
        LinkedList<GeoPoint> points = new LinkedList<>();
        for (int i = 0; i < route.size()-1; i++)
            points.addAll(getRoutePoints(route.get(i), route.get(i+1)));
        return points;
    }    
    
    
    private void removeTooDistantCaches() {
        for (int i = 0; i < caches.size(); i++) {
            GeoCache c = caches.get(i);
            if (maxLength < countDistanceToRef(c) + countDistanceFromRef(c)) {
                removeCache(c);
                i--;
            }
        }
    }
    
    private void removeCache(GeoCache cache) {
        int index = caches.indexOf(cache) + 1;
        
        double[][] tmpDistanceMatrix = new double[nodes-1][nodes-1];
        long[][] tmpTimeMatrix = new long[nodes-1][nodes-1];
        List[][] tmpRouteMatrix = new LinkedList[nodes-1][nodes-1];
        
        int i2 = 0, j2;
        for (int i = 0; i < nodes; i++) {
            if (i == index)
                continue;
            j2 = 0;
            for (int j = 0; j < nodes; j++) {
                if (j == index)
                    continue;
                tmpDistanceMatrix[i2][j2] = distanceMatrix[i][j];
                tmpTimeMatrix[i2][j2] = timeMatrix[i][j];
                tmpRouteMatrix[i2][j2] = routeMatrix[i][j];
                j2++;
            }
            i2++;
        }
        
        distanceMatrix = tmpDistanceMatrix;
        timeMatrix = tmpTimeMatrix;
        routeMatrix = tmpRouteMatrix;
        caches.remove(index - 1);
        nodes--;
        rankCaches();
    }
    
    private void rankCaches() {
        rankedCaches = new RankedCacheList();
        for (GeoCache c : caches) {
            RankedCache rc = new RankedCache(c);
            rc.setRefDistanceRank(rankRefDistance(c));
            rc.setOthDistanceRank(rankOthDistance(c));
            rc.setContainerRank(rankContainer(c));
            rc.setDifficultyRank(rankDifficulty(c));
            rc.setTerrainRank(rankTerrain(c));
            rc.setFavoritesRank(c.getFavorites());
            rankedCaches.add(rc);
        }
        rankedCaches.normalize();
    }    
    
    private double countDistanceFromRef(GeoCache to) {
        return distanceMatrix[0][caches.indexOf(to)+1];
    }
    
    private double countDistanceToRef(GeoCache from) {
        return distanceMatrix[caches.indexOf(from)+1][0];
    }
    
    private double rankRefDistance(GeoCache to) {
        return countDistanceFromRef(to);
    }
    
    private double rankOthDistance(GeoCache from) {
        double res = 0;
        int index = caches.indexOf(from)+1;
        for (int i = 1; i < nodes; i++)
            res += distanceMatrix[index][i];
        return res;
    }
    
    private double rankContainer(GeoCache c) {
        if (containerPriority == 0)
            return 0;
        else if (c.getContainer().getRank() == 0)
            return 2.5;
        else if (containerPriority == 1)
            return c.getContainer().getRank();
        else
            return 5 - c.getContainer().getRank();
    }
    
    private double rankDifficulty(GeoCache c) {
        if (difficultyPriority == 0)
            return 0;
        else if (difficultyPriority == 1)
            return c.getDifficulty();
        else 
            return 10 - c.getDifficulty();
    }
    
    private double rankTerrain(GeoCache c) {
        if (terrainPriority == 0)
            return 0;
        else if (terrainPriority == 1)
            return c.getTerrain();
        else 
            return 10 - c.getTerrain();
    }
    
    private List<GeoPoint> getRoutePoints(int from, int to) {
        return routeMatrix[from][to];
    }
    
    private long getRouteTime(int from, int to) {
        return timeMatrix[from][to];
    }
    
    private long getCacheTime(int cache) {
        return caches.get(cache-1).getDifficulty() * 5 * 60 * 1000;
    }
}
