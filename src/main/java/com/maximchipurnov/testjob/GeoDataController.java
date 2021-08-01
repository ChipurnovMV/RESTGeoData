package com.maximchipurnov.testjob;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class GeoDataController{

    public static final String OSM_URL = "https://nominatim.openstreetmap.org/search?";
    public static final int TIMEOUT = 3000;

    public ArrayList<String> getJSON(String url, int timeout) {
        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.connect();
            int status = connection.getResponseCode();

            switch (status) {
                case 200, 201 -> {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    JSONParser parser = new JSONParser();
                    JSONArray response = (JSONArray) parser.parse(reader);
                    ArrayList<String> result = new ArrayList<>();
                    for (Object o : response) {
                        result.add(new Response((JSONObject) o).toString());
                    }
                    return result;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    private static String generateAttrs(String state, String county, String city) {
        ArrayList<String> attrs = new ArrayList<>();
        attrs.add("country=russia");
        if (state != null)
            attrs.add("state=" + state);
        if (county != null)
            attrs.add("county=" + county);
        if (city != null)
            attrs.add("city=" + city);
        attrs.add("format=json&polygon_geojson=1");
        return String.join("&", attrs);
    }


    @Cacheable("fedRegName")
    @GetMapping("fedregion/{fedRegName}")
    public String getFreeQuery(@PathVariable String fedRegName) {
        String urlStr = OSM_URL + "q=" + fedRegName + "&format=json&polygon_geojson=1";
        Object object = getJSON(urlStr, TIMEOUT);
        return object.toString();
    }

    @Cacheable("state_name")
    @GetMapping("state/{state_name}")
    public String getState(@PathVariable String state_name) {
        String urlStr = OSM_URL + generateAttrs(state_name, null, null);
        Object object = getJSON(urlStr, TIMEOUT);
        return object.toString();
    }

    @Cacheable("countyName")
    @GetMapping("county/{countyName}")
    public String getCounty(@PathVariable String countyName) {
        String urlStr = OSM_URL + generateAttrs(null, countyName, null);
        Object object = getJSON(urlStr, TIMEOUT);
        return object.toString();
    }

    @Cacheable("cityName")
    @GetMapping("city/{cityName}")
    public String getCity(@PathVariable String cityName) {
        String urlStr = OSM_URL + generateAttrs(null, null, cityName);
        Object object = getJSON(urlStr, TIMEOUT);
        return object.toString();
    }

}
