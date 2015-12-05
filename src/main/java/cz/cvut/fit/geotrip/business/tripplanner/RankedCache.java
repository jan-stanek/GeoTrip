package cz.cvut.fit.geotrip.business.tripplanner;

import cz.cvut.fit.geotrip.data.entities.GeoCache;

/**
 *
 * @author jan
 */
public class RankedCache implements Comparable<RankedCache> {

    final static double REF_DISTANCE_WEIGHT = 7;
    final static double OTH_DISTANCE_WEIGHT = 15;
    final static double CONTAINER_WEIGHT = 10;
    final static double DIFFICULTY_WEIGHT = 10;
    final static double TERRAIN_WEIGHT = 10;
    final static double FAVORITES_WEIGHT = 2;
    
    GeoCache geoCache;
    double totalRank;
    
    double refDistanceRank = 0;
    double othDistanceRank = 0;
    double containerRank = 0;
    double difficultyRank = 0;
    double terrainRank = 0;
    double favoritesRank = 0;

    public RankedCache(GeoCache geoCache) {
        this.geoCache = geoCache;
    }
    
    public double getTotalRank() {
        return totalRank;
    }
    
    public GeoCache getGeoCache() {
        return geoCache;
    }
    
    public void updateTotalRank() {
        totalRank = refDistanceRank * REF_DISTANCE_WEIGHT +
                othDistanceRank * OTH_DISTANCE_WEIGHT + 
                containerRank * CONTAINER_WEIGHT +
                difficultyRank * DIFFICULTY_WEIGHT + 
                terrainRank * TERRAIN_WEIGHT +
                favoritesRank * FAVORITES_WEIGHT;
    }

    public void setRefDistanceRank(double refDistanceRank) {
        this.refDistanceRank = refDistanceRank;
        updateTotalRank();
    }

    public double getRefDistanceRank() {
        return refDistanceRank;
    }
    
    public void setOthDistanceRank(double othDistanceRank) {
        this.othDistanceRank = othDistanceRank;
        updateTotalRank();
    }

    public double getOthDistanceRank() {
        return othDistanceRank;
    }
        
    public void setContainerRank(double containerRank) {
        this.containerRank = containerRank;
        updateTotalRank();
    }

    public double getContainerRank() {
        return containerRank;
    }
    
    public void setDifficultyRank(double difficultyRank) {
        this.difficultyRank = difficultyRank;
        updateTotalRank();
    }

    public double getDifficultyRank() {
        return difficultyRank;
    }

    public void setTerrainRank(double terrainRank) {
        this.terrainRank = terrainRank;
        updateTotalRank();
    }

    public double getTerrainRank() {
        return terrainRank;
    }

    public void setFavoritesRank(double favoritesRank) {
        this.favoritesRank = favoritesRank;
        updateTotalRank();
    }    

    public double getFavoritesRank() {
        return favoritesRank;
    }

    @Override
    public int compareTo(RankedCache o) {
        return totalRank < o.totalRank ? -1 : 1;
    }
   
}
