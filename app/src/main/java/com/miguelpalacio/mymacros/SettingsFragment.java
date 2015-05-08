package com.miguelpalacio.mymacros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
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
    public static final String KEY_WEEKDAY = "pref_weekday";
    public static final String KEY_GDRIVE = "pref_gdrive";

    private ListPreference weight;
    private ListPreference height;
    private ListPreference energy;
    private ListPreference liquid;
    private ListPreference weekday;
    private ListPreference gdrive;

    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preferences);

        // Get a reference to the preferences.
        weight = (ListPreference) getPreferenceScreen().findPreference(KEY_WEIGHT);
        height = (ListPreference) getPreferenceScreen().findPreference(KEY_HEIGHT);
        energy = (ListPreference) getPreferenceScreen().findPreference(KEY_ENERGY);
        liquid = (ListPreference) getPreferenceScreen().findPreference(KEY_LIQUID);
        weekday = (ListPreference) getPreferenceScreen().findPreference(KEY_WEEKDAY);
        gdrive = (ListPreference) getPreferenceScreen().findPreference(KEY_GDRIVE);

        // Load the SharedPreferences file.
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up the initial summary for the preferences.
        // Second parameter empty: default values are defined on preferences.xml.
        weight.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_WEIGHT, ""));
        height.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_HEIGHT, ""));
        energy.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_ENERGY, ""));
        liquid.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_LIQUID, ""));
        weekday.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_WEEKDAY, ""));
        gdrive.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_GDRIVE, ""));

        // Set up a listener whenever a key changes.
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister the listener.
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_WEIGHT)) {

            double w = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_WEIGHT,""));
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String units = getPreferenceScreen().getSharedPreferences().getString(key, "");
            w = units.equals("lb") ? w * 2.2046 : w / 2.2046;

            // Commit change to profile data.
            editor.putString(ProfileFragment.KEY_WEIGHT, decimalFormat.format(w)).apply();

            // Change summary.
            weight.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));

        } else if (key.equals(KEY_HEIGHT)) {
            height.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        } else if (key.equals(KEY_ENERGY)) {
            energy.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        } else if (key.equals(KEY_LIQUID)) {
            liquid.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        } else if (key.equals(KEY_WEEKDAY)) {
            weekday.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        } else if (key.equals(KEY_GDRIVE)) {
            gdrive.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        }
    }

}
