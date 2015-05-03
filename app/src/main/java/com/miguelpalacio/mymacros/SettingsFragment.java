package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * @author Miguel Palacio
 * Some sections are taken from: http://www.phonesdevelopers.com/1745947/
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_WEIGHT_PREFERENCE = "pref_weight";
    public static final String KEY_HEIGHT_PREFERENCE = "pref_height";
    public static final String KEY_ENERGY_PREFERENCE = "pref_energy";
    public static final String KEY_LIQUID_PREFERENCE = "pref_liquid";
    public static final String KEY_WEEKDAY_PREFERENCE = "pref_weekday";
    public static final String KEY_GDRIVE_PREFERENCE = "pref_gdrive";

    private ListPreference weightPreference;
    private ListPreference heightPreference;
    private ListPreference energyPreference;
    private ListPreference liquidPreference;
    private ListPreference weekdayPreference;
    private ListPreference gdrivePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preferences);

        // Get a reference to the preferences.
        weightPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_WEIGHT_PREFERENCE);
        heightPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_HEIGHT_PREFERENCE);
        energyPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_ENERGY_PREFERENCE);
        liquidPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_LIQUID_PREFERENCE);
        weekdayPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_WEEKDAY_PREFERENCE);
        gdrivePreference = (ListPreference) getPreferenceScreen().findPreference(KEY_GDRIVE_PREFERENCE);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up the initial values.
        weightPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_WEIGHT_PREFERENCE, ""));
        heightPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_HEIGHT_PREFERENCE, ""));
        energyPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_ENERGY_PREFERENCE, ""));
        liquidPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_LIQUID_PREFERENCE, ""));
        weekdayPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_WEEKDAY_PREFERENCE, ""));
        gdrivePreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_GDRIVE_PREFERENCE, ""));

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
        if (key.equals(KEY_WEIGHT_PREFERENCE)) {
            weightPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        } else if (key.equals(KEY_HEIGHT_PREFERENCE)) {
            heightPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        } else if (key.equals(KEY_ENERGY_PREFERENCE)) {
            energyPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        } else if (key.equals(KEY_LIQUID_PREFERENCE)) {
            liquidPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        } else if (key.equals(KEY_WEEKDAY_PREFERENCE)) {
            weekdayPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        } else if (key.equals(KEY_GDRIVE_PREFERENCE)) {
            gdrivePreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
        }
    }

}
