/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.geopoint;

import org.mapsforge.core.model.LatLong;

/**
 *
 * @author jan
 */
public class GeoPoint {
    public final LatLong coordinates;
    public final String name;
    
    public GeoPoint(LatLong coordinates, String name) {
        this.coordinates = coordinates;
        this.name = name;
    }
    
    public double countDistance(GeoPoint point) {
        return countDistance(point.coordinates.latitude, point.coordinates.longitude);
    }
    
    public double countDistance(LatLong point) {
        return countDistance(point.latitude, point.longitude);
    }

    private double countDistance(double refLat, double refLon) {
        double lat = coordinates.latitude;
        double lon = coordinates.longitude;
        
        double dLat = Math.toRadians(refLat-lat);
        double dLon = Math.toRadians(refLon-lon);
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(Math.toRadians(refLat)) * Math.cos(Math.toRadians(lat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6378000 * c;
    }
}
