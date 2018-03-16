package com.github.tututelegrambot;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by maxtar on 07.03.18.
 */
class GoogleApiHandler {

    // TODO HANDLE STATUS MSG
    // todo add methods to change radius
    private static final String apiKey = "apiKey";

    private static String AUTOCOMPLETE_RADIUS = "5000";
    private static String RADAR_RADIUS = "500";
    private static String LANGUAGE = "ru";

    private static String readAll(final Reader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject read(final String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            final String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    private static String encodeParams(final Map<String, String> params) {
        return Joiner.on('&').join(
                params.entrySet().stream().map(input -> {
                    try {
                        return input.getKey() + '=' +
                                URLEncoder.encode(input.getValue(), "utf-8");
                    } catch (final UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()));
    }

    static List<String> autocomplete(String input, String location) throws IOException {
        List<String> result = new ArrayList<>();
        final String baseUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
        final Map<String, String> parameters = Maps.newHashMap();
        parameters.put("input", input);
        parameters.put("language", LANGUAGE);
        parameters.put("location", location);
        parameters.put("radius", AUTOCOMPLETE_RADIUS);
        parameters.put("key", apiKey);
        final String url = baseUrl + '?' + encodeParams(parameters);
        final JSONObject response = GoogleApiHandler.read(url);
        JSONArray predictions = response.getJSONArray("predictions");
        for (int i = 0; i < predictions.length(); i++) {
            result.add(predictions.getJSONObject(i).get("description").toString());
        }
        return result;
    }

    static JSONObject createRoute(String destination, String origin, String mode) throws IOException {
        final String baseUrl = "http://maps.googleapis.com/maps/api/directions/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("sensor", "false");
        params.put("language", "ru");
        params.put("mode", mode);
        params.put("origin", origin);
        params.put("destination", destination);
        final String url = baseUrl + '?' + encodeParams(params);
        return GoogleApiHandler.read(url);
    }

    static float[] geocoding(String address) throws IOException {
        final String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("address", address);
        params.put("region", "ru");
        params.put("language", LANGUAGE);
        params.put("key", apiKey);
        final String url = baseUrl + '?' + encodeParams(params);
        final JSONObject response = GoogleApiHandler.read(url);
        JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
        return new float[]{location.getFloat("lat"), location.getFloat("lng")};
    }

    static void textSearch(String address) throws IOException {
        final String baseUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("query", address);
        params.put("language", LANGUAGE);
        params.put("key", apiKey);
        final String url = baseUrl + '?' + encodeParams(params);
        final JSONObject response = GoogleApiHandler.read(url);
        System.out.println(response);
    }

    static List<String> radarSearch(String keyword, String location) throws IOException {
        final String baseUrl = "https://maps.googleapis.com/maps/api/place/radarsearch/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("keyword", keyword);
        params.put("name", keyword);
        params.put("language", LANGUAGE);
        params.put("radius", RADAR_RADIUS);
        params.put("location", location);
        params.put("key", apiKey);
        final String url = baseUrl + '?' + encodeParams(params);
        final JSONObject response = GoogleApiHandler.read(url);
        List<String> placesIds = new ArrayList<>();
        JSONArray results = response.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            placesIds.add(results.getJSONObject(i).getString("place_id"));
        }
        return placesIds;
    }

    static List<String> nearbySearch(String keyword, String location) throws IOException {
        final String baseUrl = "https://maps.googleapis.com/maps/api/place/radarsearch/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("keyword", keyword);
        params.put("name", keyword);
        params.put("language", LANGUAGE);
        params.put("radius", RADAR_RADIUS); // todo check rankby
        params.put("location", location);
        params.put("key", apiKey);
        final String url = baseUrl + '?' + encodeParams(params);
        final JSONObject response = GoogleApiHandler.read(url);
        List<String> placesIds = new ArrayList<>();
        JSONArray results = response.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            placesIds.add(results.getJSONObject(i).getString("place_id"));
        }
        return placesIds;
    }

    static void reverseGeocodingById(String placeId) throws IOException {
        final String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("place_id", placeId);
        params.put("language", LANGUAGE);
        params.put("key", apiKey);
        final String url = baseUrl + '?' + encodeParams(params);
        final JSONObject response = GoogleApiHandler.read(url);
        System.out.println(response);
    }

    static String placeDetailsById(String placeId) throws IOException {
        final String baseUrl = "https://maps.googleapis.com/maps/api/place/details/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("place_id", placeId);
        params.put("language", LANGUAGE);
        params.put("key", apiKey);
        final String url = baseUrl + '?' + encodeParams(params);
        final JSONObject response = GoogleApiHandler.read(url);
        JSONObject result = response.getJSONObject("result");
        return result.getString("name") + " (" + result.getString("formatted_address") + ")";
    }

    public static String getAutocompleteRadius() {
        return AUTOCOMPLETE_RADIUS;
    }

    public static void setAutocompleteRadius(String autocompleteRadius) {
        AUTOCOMPLETE_RADIUS = autocompleteRadius;
    }

    public static String getRadarRadius() {
        return RADAR_RADIUS;
    }

    public static void setRadarRadius(String radarRadius) {
        RADAR_RADIUS = radarRadius;
    }

    public static String getLANGUAGE() {
        return LANGUAGE;
    }

    public static void setLANGUAGE(String LANGUAGE) {
        GoogleApiHandler.LANGUAGE = LANGUAGE;
    }
}