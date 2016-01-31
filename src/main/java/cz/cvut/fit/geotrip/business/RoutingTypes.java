/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.business;

import com.google.common.io.Resources;
import java.util.ResourceBundle;

/**
 *
 * @author jan
 */
public enum RoutingTypes {
    FOOT {
        @Override
        public String toString() {
            return ResourceBundle.getBundle("texts").getString("routingFoot");
        }
    },
    
    BIKE {
        @Override
        public String toString() {
            return ResourceBundle.getBundle("texts").getString("routingBike");
        }
    },
    
    CAR {
        @Override
        public String toString() {
            return ResourceBundle.getBundle("texts").getString("routingCar");
        }
    };
}
