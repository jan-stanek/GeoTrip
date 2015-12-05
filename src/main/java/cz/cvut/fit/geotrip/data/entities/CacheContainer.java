package cz.cvut.fit.geotrip.data.entities;

/**
 *
 * @author jan
 */
public enum CacheContainer {

    MICRO(1, 1, "mikro"),
    SMALL(2, 2,"malá"),
    REGULAR(4, 3, "střední"),
    LARGE(8, 4, "velká"),
    OTHER(16, 0, "ostatní");
    
    private final int value;
    private final int rank;
    private final String name;

    
    private CacheContainer(int value, int rank, String name) {
        this.value = value;
        this.rank = rank;
        this.name = name;
    }
    
    public int getValue() {
        return value;
    }
    
    public int getRank() {
        return rank;
    }
    
    public String getName() {
        return name;
    }

}
