package com.maximchipurnov.testjob;

public class Coordinate {
    private double latitude;
    private double longitude;

    public Coordinate(double longitude, double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public static Coordinate lerp(Coordinate from, Coordinate to, double weight) {
        double longitude = from.getLongitude() * (1 - weight) + to.getLongitude() * weight;
        double latitude = from.getLatitude() * (1 - weight) + to.getLatitude() * weight;
        return new Coordinate(longitude, latitude);
    }

    public Coordinate lerp(Coordinate to, double weight) {
        return Coordinate.lerp(this, to, weight);
    }

    @Override
    public String toString() {
        return "[" + latitude + ", " + longitude + "]";
    }
}
