package cz.cvut.fit.geotrip.data;

import java.text.DecimalFormat;
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

    
    public GeoCache(LatLong coordinates, String name, CacheContainer container, int difficulty,
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

    public double getDistance(GeoPoint geoPoint) {
        return countDistance(geoPoint);
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

