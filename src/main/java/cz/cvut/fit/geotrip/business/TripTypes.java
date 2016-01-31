/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.business;

import java.util.ResourceBundle;

/**
 *
 * @author jan
 */
public enum TripTypes {
    NORMAL {
        @Override
        public String toString() {
            return ResourceBundle.getBundle("texts").getString("tripNormal");
        }
    },
    
    COUNT{
        @Override
        public String toString() {
            return ResourceBundle.getBundle("texts").getString("tripCount");
        }
    },
    
    PREFERENCES{
        @Override
        public String toString() {
            return ResourceBundle.getBundle("texts").getString("tripPreferences");
        }
    },
    
    FAVORITES{
        @Override
        public String toString() {
            return ResourceBundle.getBundle("texts").getString("tripFavorites");
        }
    };
}
