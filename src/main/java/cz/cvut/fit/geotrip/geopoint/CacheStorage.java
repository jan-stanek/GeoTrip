/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.geopoint;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jan
 */
public class CacheStorage {
    private List<Cache> caches;

    public CacheStorage() {
        caches = new LinkedList<>();
    }

    public void addCaches(List<Cache> list) {
        caches.addAll(list);
    }
    
    public List<Cache> getCacheList() {
        return caches;
    }
    
    public List<Cache> getFilteredList(boolean found, int container, int difficultyLow, int difficultyHigh, int terrainLow, int terrainHigh) {
        List<Cache> list = new LinkedList<>();
        
        for (Cache cache : caches) {
            if (!found && cache.found)
                continue;
            
            if ((cache.container.getValue() & container) == 0)
                continue;
            
            if (cache.difficulty < difficultyLow || cache.difficulty > difficultyHigh)
                continue;
            
            if (cache.terrain < terrainLow || cache.terrain > terrainHigh)
                continue;
            
            list.add(cache);
        }
        
        return list;
    }
}
