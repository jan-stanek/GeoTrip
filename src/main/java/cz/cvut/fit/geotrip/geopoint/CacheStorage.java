/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.geopoint;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.mapsforge.map.layer.Layer;

/**
 *
 * @author jan
 */
public class CacheStorage {
    private List<GeoCache> caches;

    public CacheStorage() {
        caches = new LinkedList<>();
    }

    public void addCaches(List<GeoCache> list) {
        caches.addAll(list);
    }
    
    public List<GeoCache> getCacheList() {
        return caches;
    }
    
    public List<GeoCache> getFilteredList(boolean found, int container, int difficultyLow, int difficultyHigh, int terrainLow, int terrainHigh) {
        List<GeoCache> list = new LinkedList<>();
        
        for (GeoCache cache : caches) {
            if (!found && cache.isFound())
                continue;
            
            if ((cache.getContainer().getValue() & container) == 0)
                continue;
            
            if (cache.getDifficulty() < difficultyLow || cache.getDifficulty() > difficultyHigh)
                continue;
            
            if (cache.getTerrain() < terrainLow || cache.getTerrain() > terrainHigh)
                continue;
            
            list.add(cache);
        }
        
        return list;
    }
}
