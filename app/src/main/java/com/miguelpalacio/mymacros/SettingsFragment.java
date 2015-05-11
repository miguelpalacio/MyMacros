package com.miguelpalacio.mymacros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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
    public static final String KEY_WEEKDAY = "pref_weekday";
    public static final String KEY_GDRIVE = "pref_gdrive";

    private ListPreference weight;
    private ListPreference height;
    private ListPreference energy;
    private ListPreference liquid;
    private ListPreference weekday;
    private ListPreference gdrive;

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
        weekday = (ListPreference) findPreference(KEY_WEEKDAY);
        gdrive = (ListPreference) findPreference(KEY_GDRIVE);

/*        // Load the SharedPreferences file.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());*/
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
        weekday.setSummary(screenPreferences.getString(KEY_WEEKDAY, ""));
        gdrive.setSummary(screenPreferences.getString(KEY_GDRIVE, ""));

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
                double w = Double.parseDouble(sharedPreferences.getString(ProfileFragment.KEY_WEIGHT, ""));
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Update the current value for weight in user profile data.
                String units = sharedPreferences.getString(key, "");
                w = units.equals("lb") ? w * 2.2046 : w / 2.2046;
                editor.putString(ProfileFragment.KEY_WEIGHT, decimalFormat.format(w)).apply();

                // Change summary.
                weight.setSummary(units);
                break;

            case KEY_HEIGHT:
                height.setSummary(sharedPreferences.getString(key, ""));
                break;

            case KEY_ENERGY:
                energy.setSummary(sharedPreferences.getString(key, ""));
                break;

            case KEY_LIQUID:
                liquid.setSummary(sharedPreferences.getString(key, ""));
                break;

            case KEY_WEEKDAY:
                weekday.setSummary(sharedPreferences.getString(key, ""));
                break;

            case KEY_GDRIVE:
                gdrive.setSummary(sharedPreferences.getString(key, ""));
                break;

            default: break;
        }
    }

}
