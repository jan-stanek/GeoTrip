package cz.cvut.fit.geotrip.business;

public interface TripType {

    double getRefDistanceWeight();

    double getOthDistanceWeight();

    double getContainerWeight();

    double getDifficultyWeight();

    double getTerrainWeight();

    double getFavoritesWeight();
}
