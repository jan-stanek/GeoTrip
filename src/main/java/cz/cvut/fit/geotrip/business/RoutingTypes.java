package cz.cvut.fit.geotrip.business;

import java.util.ResourceBundle;

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
