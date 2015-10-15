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
public class GeoCache extends GeoPoint {
    
    private final CacheContainer container;
    private final int difficulty;
    private final int terrain;
    private final int favorites;
    private final boolean found;
    private final String id;
    private final String link;
    private final double distance;
    
    public GeoCache(LatLong coordinates, String name, CacheContainer container, int difficulty, int terrain, int favorites, boolean found, String id, String link) {
        super(coordinates, name);
        
        this.container = container;
        this.difficulty = difficulty;
        this.terrain = terrain;
        this.favorites = favorites;
        this.found = found;
        this.id = id;
        this.link = link;
        
        distance = countDistance(GeoTrip.getRefPoint());
    }

    public double getDistance() {
        return distance;
    }

    public CacheContainer getContainer() {
        return container;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getTerrain() {
        return terrain;
    }

    public int getFavorites() {
        return favorites;
    }

    public boolean isFound() {
        return found;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }
}

