package cz.cvut.fit.geotrip.business;

public interface TripType {
    
    /**
     * Returns weight of distance from ref.
     * 
     * @return weight of distance from ref
     */
    double getRefDistanceWeight();

    /**
     * Returns weight of distance from others.
     * 
     * @return weight of distance from others
     */
    double getOthDistanceWeight();

    /**
     * Returns weight of container size.
     * 
     * @return weight of container size
     */
    double getContainerWeight();

    /**
     * Returns weight of difficulty.
     * 
     * @return weight of difficulty
     */
    double getDifficultyWeight();

    /**
     * Returns weight of terrain.
     * 
     * @return weight of terrain
     */
    double getTerrainWeight();

    /**
     * Returns weight of favorites.
     * 
     * @return weight of favorites
     */
    double getFavoritesWeight();
}
