package cz.cvut.fit.geotrip.data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.mapsforge.core.model.LatLong;

/**
 *
 * @author jan
 */
public class GeoPoint {

    private final LatLong coordinates;
    private final String name;
    
    public GeoPoint(LatLong coordinates, String name) {
        this.coordinates = coordinates;
        this.name = name;
    }
    
    public LatLong getCoordinates() {
        return coordinates;
    }
    
    public double getLat() {
        return coordinates.latitude;
    }
    
    public double getLon() {
        return coordinates.longitude;
    }
    
    public String getName() {
        return name;
    }
    
    public double countDistance(GeoPoint point) {
        return countDistance(point.coordinates.latitude, point.coordinates.longitude);
    }
    
    public double countDistance(LatLong point) {
        return countDistance(point.latitude, point.longitude);
    }

    public double countDistance(double refLat, double refLon) {
        double lat = coordinates.latitude;
        double lon = coordinates.longitude;
        
        double dLat = Math.toRadians(refLat-lat);
        double dLon = Math.toRadians(refLon-lon);
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(Math.toRadians(refLat)) * Math.cos(Math.toRadians(lat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6378000 * c;
    }
    
    public String getCoordinatesString() {
        double lat = coordinates.latitude;
        double lon = coordinates.longitude;
        
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
