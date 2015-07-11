package com.miguelpalacio.mymacros.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.miguelpalacio.mymacros.MainActivity;
import com.miguelpalacio.mymacros.custom.components.TwoInputPreference;
import com.miguelpalacio.mymacros.views.ProfileFragment;
import com.miguelpalacio.mymacros.views.SettingsFragment;
import com.miguelpalacio.mymacros.custom.components.SingleLinePreference;
import com.miguelpalacio.mymacros.custom.components.SingleLineTextPreference;
import com.miguelpalacio.mymacros.helpers.Utilities;
import com.miguelpalacio.mymacros.model.NutritionFormulae;
import com.miguelpalacio.mymacros.model.UnitsConverter;

/**
 * Presenter for the ProfileFragment.
 */
public class ProfilePresenter {

    static final String defaultSummary1 = "Tap to set";
    static final String defaultSummary2 = "Waiting for data";

    Context context;
    SharedPreferences sharedPref;

    UnitsConverter unitsConverter;
    NutritionFormulae formulae;

    public ProfilePresenter(Context context) {

        this.context = context;

        // Load the global SharedPreferences file.
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Instantiate needed classes from model.
        unitsConverter = new UnitsConverter(context);
        formulae = new NutritionFormulae(context);
    }


    // Set summary for a generic ListPreference.
    public void setListPrefSummary(ListPreference p, String key, String defVal) {
        if (p.getValue().equals(defVal)) {
            p.setSummary(defaultSummary1);
        } else {
            p.setSummary(sharedPref.getString(key, ""));
        }
    }

    public String setAgeSummary(String age) {
        if (age.equals("0")) {
            return defaultSummary1;
        } else {
            return sharedPref.getString(ProfileFragment.KEY_AGE, "0");
        }
    }

    public void setHeightSummary(PreferenceCategory yourData, EditTextPreference height,
                                  TwoInputPreference heightEng) {

        String unitsHeight = sharedPref.getString(SettingsFragment.KEY_HEIGHT, "");

        if (unitsHeight.equals("cm")) {
            yourData.addPreference(height);
            yourData.removePreference(heightEng);
            if (height.getText().equals("0")) {
                height.setSummary(defaultSummary1);
            } else {
                height.setSummary(sharedPref.getString(ProfileFragment.KEY_HEIGHT, "0") + " cm");
            }
        } else {
            yourData.addPreference(heightEng);
            yourData.removePreference(height);
            String value = heightEng.getValue();
            if (value.equals("0-0")) {
                heightEng.setSummary(defaultSummary1);
            } else {
                String[] values = value.split("-");
                heightEng.setSummary(values[0] + " ft, " + values[1] + " in");
            }
        }
    }

    public String setWeightSummary(String weight) {

        if (weight.equals("0")) {
            return defaultSummary1;
        }

        double w = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_WEIGHT, "0"));
        String unitsWeight = sharedPref.getString(SettingsFragment.KEY_WEIGHT, "");

        if (unitsWeight.equals("kg")) {
            return (Utilities.decimalFormat.format(w) + " kg");
        } else {
            return (Utilities.decimalFormat.format(w) + " lb");
        }
    }

    public void setMacroRateSummary(EditTextPreference p) {
        p.setSummary(p.getText() + " %");
    }

    /**
     * Defines the BMR according to the data input by the user.
     */
    public String getBmrSummary() {

        String gender = sharedPref.getString(ProfileFragment.KEY_GENDER, "ND");
        String age = sharedPref.getString(ProfileFragment.KEY_AGE, "0");
        String height = sharedPref.getString(ProfileFragment.KEY_HEIGHT, "0");
        String heightEng = sharedPref.getString(ProfileFragment.KEY_HEIGHT_ENG, "0-0");
        String weight = sharedPref.getString(ProfileFragment.KEY_WEIGHT, "0");

        // If there's missing data, return the default summary.
        if (gender.equals("ND") || age.equals("0") || (height.equals("0") &&
                heightEng.equals("0-0")) || weight.equals("0")) {
            return defaultSummary2;
        }

        String unitsHeight = sharedPref.getString(SettingsFragment.KEY_HEIGHT, "");
        String unitsWeight = sharedPref.getString(SettingsFragment.KEY_WEIGHT, "");
        String unitsEnergy = sharedPref.getString(SettingsFragment.KEY_ENERGY, "");

        int a;
        double h;
        double w;
        double BMR;

        // Get age.
        a = Integer.parseInt(age);

        // Get height and convert it (if necessary) into cm.
        if (unitsHeight.equals("cm")) {
            h = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_HEIGHT, "0"));
        } else {
            String[] values = sharedPref.getString(ProfileFragment.KEY_HEIGHT_ENG, "0-0").split("-");
            double ft = Double.parseDouble(values[0]);
            double in = Double.parseDouble(values[1]);
            h = unitsConverter.feetToCentimeters(ft, in);
        }

        // Get weight and convert it (if necessary) into kg.
        w = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_WEIGHT, "0"));
        if (!unitsWeight.equals("kg"))
            w = unitsConverter.kilogramsToPounds(w);

        // Calculate BMR for women.
        if (gender.equals("Female")) {
            BMR = formulae.getBMR(w, h, a, NutritionFormulae.FEMALE);
        }
        // Calculate BMR for men.
        else {
            BMR = formulae.getBMR(w, h, a, NutritionFormulae.MALE);
        }

        // Adjust BMR according to units.
        if (unitsEnergy.equals("kJ"))
            BMR = unitsConverter.caloriesToJoules(BMR);

        // Store the bmr preference value.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ProfileFragment.KEY_BMR, "" + BMR).apply();

        return ((int) BMR + " " + unitsEnergy);
    }


    /**
     * Set the Daily Energy need (TDEE).
     */
    public String getEnergyNeedSummary() {

        double BMR = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_BMR, "0"));
        String activityLevel = sharedPref.getString(ProfileFragment.KEY_ACTIVITY_LEVEL, "ND");
        String goal = sharedPref.getString(ProfileFragment.KEY_GOAL, "ND");

        // If there's missing data, return the default summary.
        if (activityLevel.equals("ND") || goal.equals("ND") || BMR == 0) {
            return defaultSummary2;
        }

        double TDEE = formulae.getTDEE(BMR, activityLevel, goal);

        // Store the TDEE preference value.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ProfileFragment.KEY_ENERGY_NEED, Utilities.decimalFormat.format(TDEE)).apply();

        // Update Navigation Drawer Header's Today progress label:
        MainActivity activity = (MainActivity) context;
        activity.setDrawerHeaderProgress("");

        String unitsEnergy = sharedPref.getString(SettingsFragment.KEY_ENERGY, "");

        // Set summary.
        return ((int) TDEE + " " + unitsEnergy);
    }

    public void checkMacroDistribution(boolean goalChanged) {
        // Check that macros weren't change programmatically by setting a new goal.
        if (goalChanged) {
            return;
        }
        // Check that proteinRate + carbsRate + fatRate = 100.
        int p = Integer.parseInt(sharedPref.getString(ProfileFragment.KEY_PROTEIN_RATE, "0"));
        int c = Integer.parseInt(sharedPref.getString(ProfileFragment.KEY_CARBS_RATE, "0"));
        int f = Integer.parseInt(sharedPref.getString(ProfileFragment.KEY_FAT_RATE, "0"));
        if (formulae.isValidMacroDistribution(p, c, f)) {
            Toast.makeText(context, "Correct distribution", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Macronutrient distribution is not 100 %", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets the daily macronutrient intake in grams.
     */
    public void setMacroIntakeSummaries(SingleLinePreference proteinGrams,
                                      SingleLinePreference carbsGrams, SingleLinePreference fatGrams) {

        double TDEE;
        if (sharedPref.getString(ProfileFragment.KEY_ENERGY_NEED, "0") != null) {
            TDEE = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_ENERGY_NEED, "0"));
        } else {
            return;
        }

        double protein, carbs, fat;

        protein = TDEE * Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_PROTEIN_RATE, "0")) / 100;
        carbs = TDEE * Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_CARBS_RATE, "0")) / 100;
        fat = TDEE * Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_FAT_RATE, "0")) / 100;

        String unitsEnergy = sharedPref.getString(SettingsFragment.KEY_ENERGY, "");

        // Convert to kcal if needed.
        if (unitsEnergy.equals("kJ")) {
            protein = unitsConverter.joulesToCalories(protein);
            carbs = unitsConverter.joulesToCalories(carbs);
            fat = unitsConverter.joulesToCalories(fat);
        }

        protein = unitsConverter.caloriesToGrams(protein, UnitsConverter.PROTEIN);
        carbs = unitsConverter.caloriesToGrams(carbs, UnitsConverter.CARBS);
        fat = unitsConverter.caloriesToGrams(fat, UnitsConverter.FAT);

        // Store the values in SharedPreferences.

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ProfileFragment.KEY_PROTEIN_GRAMS, Utilities.decimalFormat.format(protein)).apply();
        editor.putString(ProfileFragment.KEY_CARBS_GRAMS, Utilities.decimalFormat.format(carbs)).apply();
        editor.putString(ProfileFragment.KEY_FAT_GRAMS, Utilities.decimalFormat.format(fat)).apply();

        // Set the summaries.
        proteinGrams.setSummary((int) protein + " g/day");
        carbsGrams.setSummary((int) carbs + " g/day");
        fatGrams.setSummary((int) fat + " g/day");
    }


    // Called when the goal changes.
    public void redistributeMacroRate(SingleLineTextPreference proteinRate,
                                      SingleLineTextPreference carbsRate, SingleLineTextPreference fatRate) {

        String goal = sharedPref.getString(ProfileFragment.KEY_GOAL, "ND");
        String pRate, cRate, fRate;

        // Recommended macro distribution for each goal.
        if (goal.equals("Fat Loss")) {
            pRate = "35";
            cRate = "40";
            fRate = "25";
        } else if (goal.equals("Bulking")) {
            pRate = "20";
            cRate = "55";
            fRate = "25";
        } else {
            pRate = "30";
            cRate = "45";
            fRate = "25";
        }

        // Set the values in the corresponding preference items.
        proteinRate.setText(pRate);
        carbsRate.setText(cRate);
        fatRate.setText(fRate);
    }

    public String getFiberSummary() {

        String unitsEnergy = sharedPref.getString(SettingsFragment.KEY_ENERGY, "");

        double TDEE = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_ENERGY_NEED, "0"));
        double fiber = formulae.getFiberIntake(TDEE, unitsEnergy);

        // Store value in SharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ProfileFragment.KEY_FIBER, "" + (int) fiber).apply();

        // Set Fiber summary.
        return ((int) fiber + " g/day");
    }

    public String getWaterSummary() {

        String unitsWeight = sharedPref.getString(SettingsFragment.KEY_WEIGHT, "");
        String unitsLiquid = sharedPref.getString(SettingsFragment.KEY_LIQUID, "");
        double weight = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_WEIGHT, ""));
        double water = formulae.getWaterIntake(weight, unitsWeight, unitsLiquid);

        // Store value in SharedPreferences.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ProfileFragment.KEY_WATER, "" + (int) water).apply();

        // Set summary.
        return ((int) water + " " + unitsLiquid + "/day");
    }


}
