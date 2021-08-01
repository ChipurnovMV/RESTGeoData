package com.maximchipurnov.testjob;

import java.util.ArrayList;

public class Polygon {
    private final ArrayList<Coordinate> coordinates;

    public Polygon(ArrayList<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public ArrayList<Coordinate> get_coordinates() {
        return coordinates;
    }

    public Coordinate centroid()  {
        float centroidLatitude = 0, centroidLongitude = 0;

        for(Coordinate coordinate : coordinates) {
            centroidLatitude += coordinate.getLatitude();
            centroidLongitude += coordinate.getLongitude();
        }
        return new Coordinate(centroidLongitude / coordinates.size(),
                               centroidLatitude / coordinates.size());
    }

    public int length() {
        return coordinates.size();
    }
}
