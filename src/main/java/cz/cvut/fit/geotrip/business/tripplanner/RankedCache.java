package cz.cvut.fit.geotrip.business.tripplanner;

import cz.cvut.fit.geotrip.data.entities.GeoCache;

public class RankedCache implements Comparable<RankedCache> {

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

    public void setTotalRank(double totalRank) {
        this.totalRank = totalRank;
    }

    public void setRefDistanceRank(double refDistanceRank) {
        this.refDistanceRank = refDistanceRank;
    }

    public double getRefDistanceRank() {
        return refDistanceRank;
    }

    public void setOthDistanceRank(double othDistanceRank) {
        this.othDistanceRank = othDistanceRank;
    }

    public double getOthDistanceRank() {
        return othDistanceRank;
    }

    public void setContainerRank(double containerRank) {
        this.containerRank = containerRank;
    }

    public double getContainerRank() {
        return containerRank;
    }

    public void setDifficultyRank(double difficultyRank) {
        this.difficultyRank = difficultyRank;
    }

    public double getDifficultyRank() {
        return difficultyRank;
    }

    public void setTerrainRank(double terrainRank) {
        this.terrainRank = terrainRank;
    }

    public double getTerrainRank() {
        return terrainRank;
    }

    public void setFavoritesRank(double favoritesRank) {
        this.favoritesRank = favoritesRank;
    }

    public double getFavoritesRank() {
        return favoritesRank;
    }

    @Override
    public int compareTo(RankedCache o) {
        return totalRank < o.totalRank ? -1 : 1;
    }

}
