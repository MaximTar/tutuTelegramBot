package com.github.tututelegrambot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxtar on 14.03.18.
 */
class Utils {

    private final static int ROW_SIZE = 35;

    // this function was written for InlineKeyboardMarkup
    // but InlineKeyboardMarkup doesn't suit, because of long text
    // left it here just in case
    static String addNewLineSymbols(String input) {
        String output = "";
        String line = "";
        String word = "";

        boolean isWord = false;
        int endOfLine = input.length() - 1;

        for (int i = 0; i < input.length(); i++) {
            if (i != 0 && i % ROW_SIZE == 0) {
                if (line.startsWith(" ")) {
                    line = line.substring(1);
                }
                output += line + "\n";
                line = "";
            }
            if (Character.isLetter(input.charAt(i)) && i != endOfLine) {
                isWord = true;
                word += input.charAt(i);
            } else if (!Character.isLetter(input.charAt(i))) {
                if (isWord) {
                    line += word + input.charAt(i);
                    isWord = false;
                    word = "";
                } else {
                    line += input.charAt(i);
                }
            } else if (Character.isLetter(input.charAt(i)) && i == endOfLine) {
                line += word + input.charAt(i);
            }
        }
        if (line.startsWith(" ")) {
            line = line.substring(1);
        }
        output += line;

        return output;
    }

    static List<String> getStepsFromRoute(JSONObject route) {
        List<String> listOfSteps = new ArrayList<>();
        JSONArray steps = route.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                .getJSONObject(0).getJSONArray("steps");
        for (int i = 0; i < steps.length(); i++) {
            String duration = steps.getJSONObject(i).getJSONObject("duration").getString("text");
            String distance = steps.getJSONObject(i).getJSONObject("distance").getString("text");
            String instruction = steps.getJSONObject(i).getString("html_instructions");
            listOfSteps.add(instruction + "(" + duration + " или " + distance + ")");
        }
        return listOfSteps;
    }
}
