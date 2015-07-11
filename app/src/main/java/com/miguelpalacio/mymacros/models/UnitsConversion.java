package com.miguelpalacio.mymacros.models;

/**
 * This class provides functions for conversion.
 */
public final class UnitsConversion {

    // Height.

    public static double convertToCentimeters(double ft, double in) {
        return (ft * 12.0 + in) / 0.39370;
    }

    // Weight.

    public static double convertToPounds(double w) {
        return w / 2.2046;
    }

    // Energy.

    public static double convertToJoules(double e) {
        return e * 4.184;
    }
}
