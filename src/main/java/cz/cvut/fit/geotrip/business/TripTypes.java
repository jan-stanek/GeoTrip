package cz.cvut.fit.geotrip.business;

import java.util.ResourceBundle;

public enum TripTypes implements TripType {

    NORMAL {
                @Override
                public String toString() {
                    return ResourceBundle.getBundle("texts").getString("tripNormal");
                }

                @Override
                public double getRefDistanceWeight() {
                    return 10;
                }

                @Override
                public double getOthDistanceWeight() {
                    return 20;
                }

                @Override
                public double getContainerWeight() {
                    return 15;
                }

                @Override
                public double getDifficultyWeight() {
                    return 15;
                }

                @Override
                public double getTerrainWeight() {
                    return 15;
                }

                @Override
                public double getFavoritesWeight() {
                    return 10;
                }
            },
    COUNT {
                @Override
                public String toString() {
                    return ResourceBundle.getBundle("texts").getString("tripCount");
                }

                @Override
                public double getRefDistanceWeight() {
                    return 10;
                }

                @Override
                public double getOthDistanceWeight() {
                    return 20;
                }

                @Override
                public double getContainerWeight() {
                    return 15;
                }

                @Override
                public double getDifficultyWeight() {
                    return 15;
                }

                @Override
                public double getTerrainWeight() {
                    return 15;
                }

                @Override
                public double getFavoritesWeight() {
                    return 0;
                }
            },
    PREFERENCES {
                @Override
                public String toString() {
                    return ResourceBundle.getBundle("texts").getString("tripPreferences");
                }

                @Override
                public double getRefDistanceWeight() {
                    return 10;
                }

                @Override
                public double getOthDistanceWeight() {
                    return 20;
                }

                @Override
                public double getContainerWeight() {
                    return 50;
                }

                @Override
                public double getDifficultyWeight() {
                    return 50;
                }

                @Override
                public double getTerrainWeight() {
                    return 50;
                }

                @Override
                public double getFavoritesWeight() {
                    return 0;
                }
            },
    FAVORITES {
                @Override
                public String toString() {
                    return ResourceBundle.getBundle("texts").getString("tripFavorites");
                }

                @Override
                public double getRefDistanceWeight() {
                    return 10;
                }

                @Override
                public double getOthDistanceWeight() {
                    return 20;
                }

                @Override
                public double getContainerWeight() {
                    return 15;
                }

                @Override
                public double getDifficultyWeight() {
                    return 15;
                }

                @Override
                public double getTerrainWeight() {
                    return 15;
                }

                @Override
                public double getFavoritesWeight() {
                    return 25;
                }
            };
}
