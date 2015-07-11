package com.miguelpalacio.mymacros.model;

import android.content.Context;

/**
 * Formulas used in nutrition for energy needs estimations.
 */
public final class NutritionFormulae {

    public static int FEMALE = 0;
    public static int MALE = 1;

    Context context;

    public NutritionFormulae(Context context) {
        this.context = context;
    }

    // BMR.
    // Formula for BMR: http://www.bmi-calculator.net/bmr-calculator/bmr-formula.php

    public double getBMR(double weight, double height, double age, int gender) {
        if (gender == FEMALE) {
            return 655 + 9.6*weight + 1.8*height - 4.7*age;
        } else {
            return 66 + 13.7*weight + 5*height - 6.8*age;
        }
    }

    // TDEE (Total Daily Energy Expenditure).
    // Formula for TDEE: http://www.bmi-calculator.net/bmr-calculator/harris-benedict-equation/

    public double getTDEE(double BMR, String activityLevel, String goal) {
        double TDEE;

        switch (activityLevel) {

            case "Hyperactive":
                TDEE = BMR * 1.9;
                break;
            case "Intense":
                TDEE = BMR * 1.725;
                break;
            case "Normal":
                TDEE = BMR * 1.55;
                break;
            case "Low":
                TDEE = BMR * 1.375;
                break;
            case "Sedentary":
                TDEE = BMR * 1.2;
                break;
            default:
                TDEE = 0;
                break;
        }

        // Calculate the energy intake according to goal.
        if (goal.equals("Fat Loss")) {
            TDEE = TDEE * 0.8;
        } else if (goal.equals("Bulking")) {
            TDEE = TDEE * 1.2;
        }

        return TDEE;
    }

    // Macros.

    // Check that proteinRate + carbsRate + fatRate = 100.
    public boolean isValidMacroDistribution(int protein, int carbs, int fat) {
        return protein + carbs + fat == 100;
    }

    // Fiber.
    // Formula taken from http://healthyeating.sfgate.com/calculate-much-fiber-one-needs-day-4814.html

    public double getFiberIntake(double TDEE, String energyUnits) {

        if (energyUnits.equals("kJ")) {
            TDEE = TDEE / 4.184;
        }

        // 14 gr of fiberEditText for each 1000 kcal consumed.
        TDEE = TDEE * 14 / 1000;

        return TDEE;
    }

    // Water.
    // Formula taken from http://www.myfooddiary.com/resources/ask_the_expert/recommended_daily_water_intake.asp

    public double getWaterIntake(double weight, String weightUnits, String liquidUnits) {

        if (weightUnits.equals("kg")) {
            weight = weight * 2.2046;
        }

        // Get water intake in ounces.
        weight = weight * 0.5;

        // If units = ml, convert it.
        if (liquidUnits.equals("ml")) {
            weight = weight / 0.033814;
        }

        return weight;
    }


}
