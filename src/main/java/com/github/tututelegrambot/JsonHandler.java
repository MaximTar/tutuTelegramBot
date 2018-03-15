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
public class JsonHandler {

    // TODO HANDLE STATUS MSG
    private static final String apiKey = "apiKey";

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
        parameters.put("language", "ru");
        parameters.put("location", location);
        parameters.put("radius", "10000");
        parameters.put("key", apiKey);
        final String url = baseUrl + '?' + encodeParams(parameters);
        final JSONObject response = JsonHandler.read(url);
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
        return JsonHandler.read(url);
    }

    static float[] geocode(String address) throws IOException {
        final String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("address", address);
        params.put("region", "ru");
        params.put("language", "ru");
        params.put("key", apiKey);
        final String url = baseUrl + '?' + encodeParams(params);
        final JSONObject response = JsonHandler.read(url);
        JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
        return new float[] {location.getFloat("lat"), location.getFloat("lng")};
    }

    public static void main(final String[] args) throws IOException, JSONException {

        // TEXT SEARCH (PLACES)
//        final String baseGeoUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json";
//        final Map<String, String> geoParams = Maps.newHashMap();
//        final String address = "парк культуры";
//        geoParams.put("query", address);
//        geoParams.put("language", "ru");
//        geoParams.put("key", "apiKey");
//        final String geoUrl = baseGeoUrl + '?' + encodeParams(geoParams);
//        final JSONObject geoResponse = JsonHandler.read(geoUrl);
//        System.out.println(geoResponse);


//        autocomplete("парк культуры");
    }
}