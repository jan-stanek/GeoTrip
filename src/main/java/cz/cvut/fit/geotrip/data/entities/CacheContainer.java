package cz.cvut.fit.geotrip.data.entities;

import cz.cvut.fit.geotrip.utils.Texts;

public enum CacheContainer {

    MICRO(1, 1, Texts.getInstance().getLocalizedText("containerMicro")),
    SMALL(2, 2, Texts.getInstance().getLocalizedText("containerSmall")),
    REGULAR(4, 3, Texts.getInstance().getLocalizedText("containerRegular")),
    LARGE(8, 4, Texts.getInstance().getLocalizedText("containerLarge")),
    OTHER(16, 0, Texts.getInstance().getLocalizedText("containerOther"));

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
