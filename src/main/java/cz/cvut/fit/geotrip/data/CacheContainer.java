package cz.cvut.fit.geotrip.data;

/**
 *
 * @author jan
 */
public enum CacheContainer {

    MICRO(1, "mikro"),
    SMALL(2, "malá"),
    REGULAR(4, "střední"),
    LARGE(8, "velká"),
    OTHER(16, "ostatní");
    
    private final int value;
    private final String name;

    
    private CacheContainer(int value, String name) {
        this.value = value;
        this.name = name;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getName() {
        return name;
    }

}
