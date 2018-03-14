package com.github.tututelegrambot;

/**
 * Created by maxtar on 14.03.18.
 */
public class Utils {

    public static String addNewLineSymbols(String input) {
        String output = "";

        boolean isWord = false;
        int endOfLine = input.length() - 1;
        String word = "";

        for (int i = 0; i < input.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(input.charAt(i)) && i != endOfLine) {
                isWord = true;
                word += input.charAt(i);
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(input.charAt(i)) && isWord) {
                output += word + input.charAt(i);
                isWord = false;
                word = "";
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(input.charAt(i)) && i == endOfLine) {
                output += word + input.charAt(i);
            }
        }

        return output;
    }
}
