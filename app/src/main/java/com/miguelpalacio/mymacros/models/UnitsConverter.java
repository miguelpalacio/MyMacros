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

    public double feetToCentimeters(double ft, double in) {
        return (ft * 12.0 + in) / 0.3937;
    }

    public Integer[] centimetersToFeet(double cm) {
        double h = cm * 0.3937 / 12;
        Integer[] feetInches = new Integer[2];
        // ft: feetInches[0], in: feetInches[1].
        feetInches[0] = (int) h;
        feetInches[1] = (int) Math.round((h - feetInches[0]) * 12);
        return feetInches;
    }

    // Weight.

    public double kilogramsToPounds(double w) {
        return w / 2.2046;
    }

    public double poundsToKilograms(double w) {
        return w * 2.2046;
    }

    // Energy.

    public double caloriesToJoules(double e) {
        return e * 4.184;
    }

    public double joulesToCalories(double e) {
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
