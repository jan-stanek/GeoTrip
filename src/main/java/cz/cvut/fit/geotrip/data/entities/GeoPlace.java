package cz.cvut.fit.geotrip.data.entities;

public class GeoPlace {

    private final GeoPoint coordinates;
    private final String name;

    public GeoPlace(GeoPoint coordinates, String name) {
        this.coordinates = coordinates;
        this.name = name;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public double getLat() {
        return coordinates.getLat();
    }

    public double getLon() {
        return coordinates.getLon();
    }

    public String getName() {
        return name;
    }

    public double getCircleDistance(GeoPlace point) {
        return coordinates.getCircleDistance(point);
    }

    public double getCircleDistance(double refLat, double refLon) {
        return coordinates.getCircleDistance(refLat, refLon);
    }

    public String getCoordinatesString() {
        return coordinates.getCoordinatesString();
    }
}
