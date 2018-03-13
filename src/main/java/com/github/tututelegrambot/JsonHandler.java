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

    private static String readAll(final Reader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject read(final String url) throws IOException, JSONException {
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

    public static List<String> autocomplete(String input, String location) throws IOException {
        List<String> result = new ArrayList<>();
        final String baseUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
        final Map<String, String> parameters = Maps.newHashMap();
        parameters.put("input", input);
        parameters.put("language", "ru");
        parameters.put("location", location);
        parameters.put("radius", "10000");
        parameters.put("key", "apiKey");
        final String url = baseUrl + '?' + encodeParams(parameters);
        final JSONObject response = JsonHandler.read(url);
        JSONArray predictions = response.getJSONArray("predictions");
        for (int i = 0; i < predictions.length(); i++) {
            result.add(predictions.getJSONObject(i).get("description").toString());
        }
        return result;
    }

    public static void main(final String[] args) throws IOException, JSONException {
        // ROUTES (DIRECTIONS)
//        final String baseUrl = "http://maps.googleapis.com/maps/api/directions/json";
//        final Map<String, String> params = Maps.newHashMap();
//        params.put("sensor", "false");// указывает, исходит ли запрос на геокодирование от устройства с датчиком
//        params.put("language", "ru");// язык данные на котором мы хотим получить
//        params.put("mode", "walking");// способ перемещения, может быть driving, walking, bicycling
//        params.put("origin", "Россия, Москва, улица Поклонная, 12");// адрес или текстовое значение широты и отправного пункта маршрута
//        params.put("destination", "парк культуры");// адрес или текстовое значение широты и долготы конечного пункта маршрута
//        final String url = baseUrl + '?' + encodeParams(params);// генерируем путь с параметрами
//        System.out.println(url);
//        final JSONObject response = JsonHandler.read(url);// делаем запрос к вебсервису и получаем от него ответ
//        // как правило наиболее подходящий ответ первый и данные о координатах можно получить по пути
//        // //results[0]/geometry/location/lng и //results[0]/geometry/location/lat
//        System.out.println(response);
//        System.out.println(response.getJSONArray("geocoded_waypoints"));
//        System.out.println(response.getJSONArray("routes"));
//        JSONObject location = response.getJSONArray("routes").getJSONObject(0);
//        location = location.getJSONArray("legs").getJSONObject(0);
//        final String distance = location.getJSONObject("distance").getString("text");
//        final String duration = location.getJSONObject("duration").getString("text");
//        System.out.println(distance + "\n" + duration);

        // GEOCODING (GEOCODE)
//        final String baseGeoUrl = "https://maps.googleapis.com/maps/api/geocode/json";
//        final Map<String, String> geoParams = Maps.newHashMap();
//        final String address = "парк культуры";
//        geoParams.put("address", address);
//        geoParams.put("region", "ru");
//        geoParams.put("language", "ru");
//        geoParams.put("key", "apiKey");
//        final String geoUrl = baseGeoUrl + '?' + encodeParams(geoParams);
//        final JSONObject geoResponse = JsonHandler.read(geoUrl);
//        System.out.println(geoResponse);

        // AUTOCOMPLETE (PLACES)
//        final String baseGeoUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
//        final Map<String, String> geoParams = Maps.newHashMap();
//        final String address = "парк культуры";
//        geoParams.put("input", address);
//        geoParams.put("language", "ru");
//        geoParams.put("key", "apiKey");
//        final String geoUrl = baseGeoUrl + '?' + encodeParams(geoParams);
//        final JSONObject geoResponse = JsonHandler.read(geoUrl);
//        System.out.println(geoResponse);

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