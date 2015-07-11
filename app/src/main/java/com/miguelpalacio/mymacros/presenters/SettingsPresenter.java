package com.miguelpalacio.mymacros.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.miguelpalacio.mymacros.helpers.Utilities;
import com.miguelpalacio.mymacros.model.UnitsConverter;
import com.miguelpalacio.mymacros.views.ProfileFragment;

/**
 * Presenter for the SettingsFragment.
 */
public class SettingsPresenter {

    Context context;
    SharedPreferences sharedPref;

    UnitsConverter unitsConverter;

    public SettingsPresenter(Context context) {

        this.context = context;

        // Load the global SharedPreferences file.
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Instantiate needed classes from model.
        unitsConverter = new UnitsConverter(context);
    }


    public void updateProfileWeight(String units) {
        double w = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_WEIGHT, "0"));
        SharedPreferences.Editor editor = sharedPref.edit();

        // Update the current value for weight in user profile data.
        w = units.equals("lb") ? unitsConverter.poundsToKilograms(w) : unitsConverter.kilogramsToPounds(w);
        editor.putString(ProfileFragment.KEY_WEIGHT, Utilities.decimalFormat.format(w)).apply();
    }

    public void updateProfileHeight(String units) {
        double h;
        SharedPreferences.Editor editor = sharedPref.edit();

        // Update the current value for height in user profile data.
        if (units.equals("cm")) {
            String[] values = sharedPref.
                    getString(ProfileFragment.KEY_HEIGHT_ENG, "0-0").split("-");
            h = unitsConverter.feetToCentimeters(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
            editor.putString(ProfileFragment.KEY_HEIGHT, "" + (int) h).apply();
        } else {
            h = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_HEIGHT, "0"));
            Integer feetInches[] = unitsConverter.centimetersToFeet(h);
            editor.putString(ProfileFragment.KEY_HEIGHT_ENG, feetInches[0] + "-" + feetInches[1]).apply();
        }
    }

    public void updateProfileEnergyNeed(String units) {
        double e = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_ENERGY_NEED, "0"));
        SharedPreferences.Editor editor = sharedPref.edit();

        // Update the current value for energy need in user profile data.
        if (units.equals("kJ")) {
            e = unitsConverter.caloriesToJoules(e);
        } else {
            e = unitsConverter.joulesToCalories(e);
        }

        editor.putString(ProfileFragment.KEY_ENERGY_NEED, Utilities.decimalFormat.format(e)).apply();
    }
}
