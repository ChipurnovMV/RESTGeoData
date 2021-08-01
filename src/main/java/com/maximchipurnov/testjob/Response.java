package com.maximchipurnov.testjob;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Response {
    private final String display_name;
    private ArrayList<Polygon> polygons;
    private Coordinate center;
    private Polygon biggestPolygon;

    public Response(JSONObject object) {
        JSONObject geoData = (JSONObject) object.get("geojson");
        JSONArray coordinates = (JSONArray) geoData.get("coordinates");
        display_name = (String) object.get("display_name");
        polygons = null;
        if (coordinates.size() > 0) {
            if (coordinates.get(0) instanceof Double) // для объектов не имеющих полигонов а только точку
                center = new Coordinate((double) coordinates.get(0), (double) coordinates.get(1));
            else { // для объектов имеющих полигоны
                polygons = getPolygons(coordinates);
                setBiggestPolygon();
                setCenter();
            }
        }
    }

    private void setBiggestPolygon() {
        if (polygons.size() > 0) {
            polygons.sort((a, b) -> (b.length() - a.length()));
            biggestPolygon = polygons.get(0);
        }
    }

    private boolean isArrayOfCoordinates(JSONArray array) {
        if (array.size() > 0) {
            if (array.get(0) instanceof JSONArray subarray) {
                return subarray.get(0) instanceof Double;
            }
        }
        return false;
    }

    private ArrayList<Polygon> getPolygons(JSONArray array) {
        ArrayList<Polygon> polygons = new ArrayList<>();
        int arraySize = array.size();
        if (arraySize > 0) {
            if (isArrayOfCoordinates(array)) {
                ArrayList<Coordinate> coordinates = new ArrayList<>();
                for (Object coordinate : array)
                    coordinates.add(new Coordinate((double) ((JSONArray) coordinate).get(0),
                                                   (double) ((JSONArray) coordinate).get(1)));
                polygons.add(new Polygon(coordinates));
            } else
                for (Object subarray : array)
                    polygons.addAll(getPolygons((JSONArray) subarray));
        }
        return polygons;
    }

    private void setCenter() {
        center = null;
        if (biggestPolygon != null) {
            center = biggestPolygon.centroid();
            for (Polygon polygon : polygons) {
                if (polygon != biggestPolygon) {
                    Coordinate polCentroid = polygon.centroid();
                    double weight = polygon.length() / (float) biggestPolygon.length() / 2;
                    center = center.lerp(polCentroid, weight);
                    // weight - вес смещения центра, определяется из соотношения самого большого полигона к текущему
                    // из списка.
                    // Вес поделен на 2, так как предпологается что при одинаковых размерах полигонов центр будет
                    // находится ровно по середине между их индивидуальных центров, поэтому при одинаковых размерах
                    // weight должен быть 0.5 для получения точки ровно посередине при использовании математической
                    // функции lerp
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        JSONObject object = new JSONObject();
        object.put("display_name", display_name);
        object.put("center", center);
        if (biggestPolygon != null)
            object.put("biggestPolygon", biggestPolygon.get_coordinates());
        return object.toJSONString();
    }
}
