/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.geopoint;

import cz.cvut.fit.geotrip.GeoTrip;
import org.mapsforge.core.model.LatLong;

/**
 *
 * @author jan
 */
public class Cache extends GeoPoint {
    
    public final CacheContainer container;
    public final int difficulty;
    public final int terrain;
    public final int favorites;
    public final boolean found;
    public final String id;
    public final String link;
    public final double distance;
    
    public Cache(LatLong coordinates, String name, CacheContainer container, int difficulty, int terrain, int favorites, boolean found, String id, String link) {
        super(coordinates, name);
        
        this.container = container;
        this.difficulty = difficulty;
        this.terrain = terrain;
        this.favorites = favorites;
        this.found = found;
        this.id = id;
        this.link = link;
        
        distance = countDistance(GeoTrip.ref);
    }
    
}
