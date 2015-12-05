package cz.cvut.fit.geotrip.data.entities;

import java.text.DecimalFormat;

/**
 *
 * @author jan
 */
public class GeoCache extends GeoPlace {
    
    private final CacheContainer container;
    private final int difficulty;
    private final int terrain;
    private final int favorites;
    private final boolean found;
    private final String id;
    private final String link;

    
    public GeoCache(GeoPoint coordinates, String name, CacheContainer container, int difficulty,
            int terrain, int favorites, boolean found, String id, String link) {
        super(coordinates, name);
        
        this.container = container;
        this.difficulty = difficulty;
        this.terrain = terrain;
        this.favorites = favorites;
        this.found = found;
        this.id = id;
        this.link = link;
    }

    public double getDistance(GeoPlace geoPoint) {
        return getCircleDistance(geoPoint);
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
    
    public String getDifficultyString() {
        return new DecimalFormat("#.#").format((difficulty + 1) / 2.0);
    }
    
    public String getTerrainString() {
        return new DecimalFormat("#.#").format((terrain + 1) / 2.0);
    }
}

