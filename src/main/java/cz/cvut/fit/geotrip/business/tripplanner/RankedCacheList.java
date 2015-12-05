package cz.cvut.fit.geotrip.business.tripplanner;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jan
 */
public class RankedCacheList {
    
    private final List<RankedCache> list;

    
    public RankedCacheList() {
        list = new LinkedList<>();
    }
    
    public void add(RankedCache rc) {
        list.add(rc);
    }
    
    public RankedCache getLast() {
        return list.get(list.size()-1);
    }
    
    public void normalize() {
        double othDistanceMax = countOthDistanceMax();
        double othDistanceMin = countOthDistanceMin();
        double refDistanceMax = countRefDistanceMax();
        double refDistanceMin = countRefDistanceMin();
        double terrainMax = countTerrainMax();
        double difficultyMax = countDifficultyMax();
        double containerMax = countContainerMax();
        double favoritesMax = countFavoritesMax();
        
        for (RankedCache rc : list) {
            if (othDistanceMax != othDistanceMin)
                rc.setOthDistanceRank((othDistanceMax - rc.getOthDistanceRank()) / (othDistanceMax - othDistanceMin));
            
            if (refDistanceMax != refDistanceMin)
                rc.setRefDistanceRank((refDistanceMax - rc.getRefDistanceRank()) / (refDistanceMax - refDistanceMin));
            
            if (terrainMax != 0)
                rc.setTerrainRank(rc.getTerrainRank() / terrainMax);
            
            if (difficultyMax != 0)
                rc.setDifficultyRank(rc.getDifficultyRank() / difficultyMax);
            
            if (containerMax != 0)
                rc.setContainerRank(rc.getContainerRank() / containerMax);
            
            if (favoritesMax != 0)
                rc.setFavoritesRank(rc.getFavoritesRank() / favoritesMax);
        }

        sort();
    }

    
    private void sort() {
        list.sort(new Comparator<RankedCache>() {
            @Override
            public int compare(RankedCache rc1, RankedCache rc2) {
                return rc2.compareTo(rc1);
            }
        });
    }
    
    private double countOthDistanceMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getOthDistanceRank() > max)
                max = rc.getOthDistanceRank();
        }
        return max;
    }

    private double countOthDistanceMin() {
        double min = Double.POSITIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getOthDistanceRank() < min)
                min = rc.getOthDistanceRank();
        }
        return min;
    }
    
    private double countRefDistanceMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getRefDistanceRank() > max)
                max = rc.getRefDistanceRank();
        }
        return max;
    }
    
    private double countRefDistanceMin() {
        double min = Double.POSITIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getRefDistanceRank() < min)
                min = rc.getRefDistanceRank();
        }
        return min;
    }
    
    private double countDifficultyMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getDifficultyRank() > max)
                max = rc.getDifficultyRank();
        }
        return max;
    }

    private double countTerrainMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getTerrainRank() > max)
                max = rc.getTerrainRank();
        }
        return max;
    }
    
    private double countContainerMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getContainerRank() > max)
                max = rc.getContainerRank();
        }
        return max;
    }
    
    private double countFavoritesMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getFavoritesRank() > max)
                max = rc.getFavoritesRank();
        }
        return max;
    }    
  
}
