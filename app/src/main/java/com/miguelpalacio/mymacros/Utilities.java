package com.miguelpalacio.mymacros;

import java.text.Normalizer;
import java.util.List;

/**
 * Utility functions that can be used across the whole app.
 */
public final class Utilities {

    /**
     * Removes the accents from a string.
     * @param string string with accents.
     * @return string without accents.
     */
    public static String flattenToAscii(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        for (char c : string.toCharArray()) {
            if (c <= '\u007F') sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Removes the accent from a character.
     * @param character character with accents.
     * @return character without accent.
     */
    public static Character flattenToAscii(Character character) {
        StringBuilder sb = new StringBuilder(1);
        String string = Normalizer.normalize(character.toString(), Normalizer.Form.NFD);
        for (char c : string.toCharArray()) {
            if (c <= '\u007F') sb.append(c);
        }
        return sb.toString().charAt(0);
    }

    /**
     * Gets rid of spaces (if any) at the beginning and at the end of the given string.
     * Also capitalizes the first letter of the given string.
     * @param name the given string.
     * @return the string formatted.
     */
    public static String formatNameString(String name) {
        // Get rid of space chars at the beginning and end of the string.
        while (Character.isSpaceChar(name.charAt(0)) && name.length() > 1) {
            name = name.substring(1);
        }
        while (Character.isSpaceChar(name.charAt(name.length() - 1)) && name.length() > 1) {
            name = name.substring(0, name.length() - 1);
        }

        // Capitalize the first letter of the name string.
        if (name.length() > 1) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        } else {
            name = name.toUpperCase();
        }

        return name;
    }


    /**
     * Given an list, performs a summation with its elements and returns the result.
     */
    public static double getSummation(List<Double> list) {
        double accumulated = 0;
        for (int i = 0; i < list.size(); i++) {
            accumulated = accumulated + list.get(i);
        }
        return accumulated;
    }
}
