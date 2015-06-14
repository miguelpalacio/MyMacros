package com.miguelpalacio.mymacros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * @author Miguel Palacio
 * Some sections were taken from: http://www.phonesdevelopers.com/1745947/
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_WEIGHT = "pref_weight";
    public static final String KEY_HEIGHT = "pref_height";
    public static final String KEY_ENERGY = "pref_energy";
    public static final String KEY_LIQUID = "pref_liquid";
/*    public static final String KEY_WEEKDAY = "pref_weekday";*/
/*    public static final String KEY_GDRIVE = "pref_gdrive";*/

    private ListPreference weight;
    private ListPreference height;
    private ListPreference energy;
    private ListPreference liquid;
/*    private ListPreference weekday;*/
/*    private Preference gdrive;*/

    private SharedPreferences sharedPref;
    DecimalFormat decimalFormat = new DecimalFormat("#.#");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preferences);

        // Get a reference to the preferences.
        weight = (ListPreference) findPreference(KEY_WEIGHT);
        height = (ListPreference) findPreference(KEY_HEIGHT);
        energy = (ListPreference) findPreference(KEY_ENERGY);
        liquid = (ListPreference) findPreference(KEY_LIQUID);
/*        weekday = (ListPreference) findPreference(KEY_WEEKDAY);*/
/*        gdrive = findPreference(KEY_GDRIVE);*/

        // Load the SharedPreferences file.
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get PreferenceScreen's SharedPreferences.
        SharedPreferences screenPreferences = getPreferenceScreen().getSharedPreferences();

        // Set up the initial summary for the preferences.
        // Second parameter empty: default values are defined on preferences.xml.
        weight.setSummary(screenPreferences.getString(KEY_WEIGHT, ""));
        height.setSummary(screenPreferences.getString(KEY_HEIGHT, ""));
        energy.setSummary(screenPreferences.getString(KEY_ENERGY, ""));
        liquid.setSummary(screenPreferences.getString(KEY_LIQUID, ""));
/*        weekday.setSummary(screenPreferences.getString(KEY_WEEKDAY, ""));*/
/*        gdrive.setSummary(screenPreferences.getString(KEY_GDRIVE, ""));*/

        // Set up a listener whenever a key changes.
        screenPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister the listener.
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {

            case KEY_WEIGHT:
                double w = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_WEIGHT, "0"));
                SharedPreferences.Editor wEditor = sharedPref.edit();

                // Update the current value for weight in user profile data.
                String wUnits = sharedPref.getString(key, "");
                w = wUnits.equals("lb") ? w*2.2046 : w/2.2046;
                wEditor.putString(ProfileFragment.KEY_WEIGHT, decimalFormat.format(w)).apply();

                // Change preference summary.
                weight.setSummary(wUnits);
                break;

            case KEY_HEIGHT:
                double h;
                SharedPreferences.Editor hEditor = sharedPreferences.edit();
                String hUnits = sharedPreferences.getString(key, "");

                // Update the current value for height in user profile data.
                if (hUnits.equals("cm")) {
                    String[] values = sharedPreferences.
                            getString(ProfileFragment.KEY_HEIGHT_ENG, "0-0").split("-");
                    // Convert from ft-in into cm.
                    h = Double.parseDouble(values[0])*12 + Double.parseDouble(values[1]);
                    int cm = (int) (h/0.3937);
                    hEditor.putString(ProfileFragment.KEY_HEIGHT, "" + cm).apply();
                } else {
                    h = Double.parseDouble(sharedPreferences.getString(ProfileFragment.KEY_HEIGHT, "0"));
                    // Convert from cm into ft-in.
                    h = h*0.3937/12;
                    int ft = (int) h;
                    int in = (int) Math.round((h - ft) * 12);
                    hEditor.putString(ProfileFragment.KEY_HEIGHT_ENG, ft + "-" + in).apply();
                }

                // Change preference summary.
                height.setSummary(sharedPreferences.getString(key, ""));
                break;

            case KEY_ENERGY:
                double e;
                SharedPreferences.Editor eEditor = sharedPreferences.edit();
                String eUnits = sharedPreferences.getString(key, "");

                // Update the current value for height in user profile data.
                if (eUnits.equals("kJ")) {
                    e = Double.parseDouble(sharedPreferences.getString(ProfileFragment.KEY_ENERGY_NEED, "0"));
                    // Convert from kcal into kJ.
                    e = e * 4.184;
                    eEditor.putString(ProfileFragment.KEY_ENERGY_NEED, "" + decimalFormat.format(e)).apply();
                } else {
                    e = Double.parseDouble(sharedPreferences.getString(ProfileFragment.KEY_ENERGY_NEED, "0"));
                    // Convert from kJ into kcal.
                    e = e / 4.184;
                    eEditor.putString(ProfileFragment.KEY_ENERGY_NEED, "" + decimalFormat.format(e)).apply();
                }

                // Change preference summary.
                energy.setSummary(sharedPreferences.getString(key, ""));
                break;

            case KEY_LIQUID:
                liquid.setSummary(sharedPreferences.getString(key, ""));
                break;

/*            case KEY_WEEKDAY:
                weekday.setSummary(sharedPreferences.getString(key, ""));
                break;*/

/*            case KEY_GDRIVE:
                gdrive.setSummary(sharedPreferences.getString(key, ""));
                break;*/

            default: break;
        }
    }

/*    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {

        if (preference.getKey().equals(KEY_GDRIVE)) {

            Log.d("Preference clicked: ", preference.toString());
        }
        return true;
    }*/

}
