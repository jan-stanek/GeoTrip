package cz.cvut.fit.geotrip.business;

import cz.cvut.fit.geotrip.utils.Texts;

public enum TripTypes implements TripType {

    NORMAL {
                @Override
                public String toString() {
                    return Texts.getInstance().getLocalizedText("tripNormal");
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
                    return 20;
                }

                @Override
                public double getDifficultyWeight() {
                    return 20;
                }

                @Override
                public double getTerrainWeight() {
                    return 20;
                }

                @Override
                public double getFavoritesWeight() {
                    return 10;
                }
            },
    COUNT {
                @Override
                public String toString() {
                    return Texts.getInstance().getLocalizedText("tripCount");
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
                    return Texts.getInstance().getLocalizedText("tripPreferences");
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
                    return 10;
                }
            },
    FAVORITES {
                @Override
                public String toString() {
                    return Texts.getInstance().getLocalizedText("tripFavorites");
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
