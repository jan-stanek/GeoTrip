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
    
}
