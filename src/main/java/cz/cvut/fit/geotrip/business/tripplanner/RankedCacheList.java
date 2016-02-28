package cz.cvut.fit.geotrip.business.tripplanner;

import cz.cvut.fit.geotrip.business.TripType;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class RankedCacheList {

    private final List<RankedCache> list;

    public RankedCacheList() {
        list = new LinkedList<>();
    }

    public void add(RankedCache rc) {
        list.add(rc);
    }

    public RankedCache getLast() {
        return list.get(list.size() - 1);
    }

    public void sort(TripType tripType, double maxLength, double currentLength) {
        countTotalRank(tripType, maxLength, currentLength);
        sort();
    }
    
    private void countTotalRank(TripType tripType, double maxLength, double currentLength) {
        double othDistanceMax = countOthDistanceMax();
        double othDistanceMin = countOthDistanceMin();
        double refDistanceMax = countRefDistanceMax();
        double refDistanceMin = countRefDistanceMin();
        double terrainMax = countTerrainMax();
        double difficultyMax = countDifficultyMax();
        double containerMax = countContainerMax();
        double favoritesMax = countFavoritesMax();

        double othDistanceRank = 0;
        double refDistanceRank = 0;
        double terrainRank = 0;
        double difficultyRank = 0;
        double containerRank = 0;
        double favoritesRank = 0;
        
        double totalRank;
        double lengthWeight = maxLength / currentLength;
        
        for (RankedCache rc : list) {
            if (othDistanceMax != othDistanceMin) 
                othDistanceRank = (othDistanceMax - rc.getOthDistanceRank()) / (othDistanceMax - othDistanceMin);
            if (refDistanceMax != refDistanceMin) 
                refDistanceRank = (refDistanceMax - rc.getRefDistanceRank()) / (refDistanceMax - refDistanceMin);
            if (terrainMax != 0)
                terrainRank = rc.getTerrainRank() / terrainMax;
            if (difficultyMax != 0) 
                difficultyRank = rc.getDifficultyRank() / difficultyMax;
            if (containerMax != 0) 
                containerRank = rc.getContainerRank() / containerMax;
            if (favoritesMax != 0) 
                favoritesRank = rc.getFavoritesRank() / favoritesMax;
            
            totalRank = othDistanceRank * tripType.getOthDistanceWeight() * lengthWeight + 
                    refDistanceRank * tripType.getRefDistanceWeight() * (1 - lengthWeight) + 
                    terrainRank * tripType.getTerrainWeight() * lengthWeight + 
                    difficultyRank * tripType.getDifficultyWeight() * lengthWeight + 
                    containerRank * tripType.getContainerWeight() * lengthWeight +
                    favoritesRank * tripType.getFavoritesWeight() * lengthWeight;
                    
            rc.setTotalRank(totalRank);
        }
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
            if (rc.getOthDistanceRank() > max) {
                max = rc.getOthDistanceRank();
            }
        }
        return max;
    }

    private double countOthDistanceMin() {
        double min = Double.POSITIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getOthDistanceRank() < min) {
                min = rc.getOthDistanceRank();
            }
        }
        return min;
    }

    private double countRefDistanceMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getRefDistanceRank() > max) {
                max = rc.getRefDistanceRank();
            }
        }
        return max;
    }

    private double countRefDistanceMin() {
        double min = Double.POSITIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getRefDistanceRank() < min) {
                min = rc.getRefDistanceRank();
            }
        }
        return min;
    }

    private double countDifficultyMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getDifficultyRank() > max) {
                max = rc.getDifficultyRank();
            }
        }
        return max;
    }

    private double countTerrainMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getTerrainRank() > max) {
                max = rc.getTerrainRank();
            }
        }
        return max;
    }

    private double countContainerMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getContainerRank() > max) {
                max = rc.getContainerRank();
            }
        }
        return max;
    }

    private double countFavoritesMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (RankedCache rc : list) {
            if (rc.getFavoritesRank() > max) {
                max = rc.getFavoritesRank();
            }
        }
        return max;
    }

}
