package cz.cvut.fit.geotrip.utils;

import java.util.ResourceBundle;

public class Texts {

    private static Texts instance = null;
    ResourceBundle resourceBundle;

    private Texts() {
        resourceBundle = ResourceBundle.getBundle("texts");
    }

    public static Texts getInstance() {
        if (instance == null) {
            instance = new Texts();
        }
        return instance;
    }

    /**
     * Returns localized string by key.
     * 
     * @param key key of string
     * @return localized string
     */
    public String getLocalizedText(String key) {
        return resourceBundle.getString(key);
    }
}
