package com.miguelpalacio.mymacros.models;

import android.content.Context;

/**
 * This class provides functions for conversion.
 */
public final class UnitsConverter {

    public static final int PROTEIN = 0;
    public static final int CARBS = 1;
    public static final int FAT = 2;


    Context context;

    public UnitsConverter(Context context) {
        this.context = context;
    }

    // Height.

    public double convertToCentimeters(double ft, double in) {
        return (ft * 12.0 + in) / 0.3937;
    }

    // Weight.

    public double convertToPounds(double w) {
        return w / 2.2046;
    }

    // Energy.

    public double convertToJoules(double e) {
        return e * 4.184;
    }

    public double convertToCalories(double e) {
        return e / 4.184;
    }

    public double caloriesToGrams(double cal, int macro) {
        if (macro == PROTEIN || macro == CARBS) {
            return cal / 4;
        } else {
            return cal / 9;
        }
    }
}
