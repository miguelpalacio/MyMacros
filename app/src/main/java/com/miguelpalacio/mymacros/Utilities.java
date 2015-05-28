package com.miguelpalacio.mymacros;

import java.text.Normalizer;

/**
 * Utility functions that can be used across the whole app.
 */
public final class Utilities {

    /**
     * Remove the accents from a string.
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
     * Remove the accent from a character.
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
}
