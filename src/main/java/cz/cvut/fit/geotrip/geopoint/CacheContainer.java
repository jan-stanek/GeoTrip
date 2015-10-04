/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.geopoint;

/**
 *
 * @author jan
 */
public enum CacheContainer {
    MICRO(1),
    SMALL(2),
    REGULAR(4),
    LARGE(8),
    OTHER(16);
    
    private final int value;
    
    private CacheContainer(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
