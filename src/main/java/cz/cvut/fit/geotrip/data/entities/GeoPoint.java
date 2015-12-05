package cz.cvut.fit.geotrip.data.entities;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author jan
 */
public class GeoPoint {

    private final double lat;
    private final double lon;
    
    public GeoPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
    
    public double getLat() {
        return lat;
    }
    
    public double getLon() {
        return lon;
    }
    
    public double getCircleDistance(GeoPlace point) {
        return getCircleDistance(point.getLat(), point.getLon());
    }
    
    public double getCircleDistance(double refLat, double refLon) {
        double dLat = Math.toRadians(refLat-lat);
        double dLon = Math.toRadians(refLon-lon);
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(Math.toRadians(refLat)) * Math.cos(Math.toRadians(lat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6378000 * c;
    }
    
    public String getCoordinatesString() {
        int latDegrees = (int)Math.round(Math.floor(lat));
        int latMinutes = (int)Math.round(Math.floor(60 * (lat - latDegrees)));
        double latSeconds = 3600 * ((lat - latDegrees) - latMinutes / 60.0);
        
        char latSign = lat < 0 ? 'S' : 'N';
        
        int lonDegrees = (int)Math.round(Math.floor(lon));
        int lonMinutes = (int)Math.round(Math.floor(60 * (lon - lonDegrees)));
        double lonSeconds = 3600 * ((lon - lonDegrees) - lonMinutes / 60.0);
        
        char lonSign = lat < 0 ? 'W' : 'E';
        
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.getDefault());
        dfs.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.000", dfs);
                
        return String.format("%d°%d'%s\"%c, %d°%d'%s\"%c", latDegrees, latMinutes, df.format(latSeconds), latSign, lonDegrees, lonMinutes, df.format(lonSeconds), lonSign);
    }
}
