package com.miguelpalacio.mymacros.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.miguelpalacio.mymacros.R;
import com.miguelpalacio.mymacros.presenters.SettingsPresenter;
import com.miguelpalacio.mymacros.views.ProfileFragment;

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

    private ListPreference weight;
    private ListPreference height;
    private ListPreference energy;
    private ListPreference liquid;
/*    private ListPreference weekday;*/

    SettingsPresenter settingsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate a presenter for this view.
        settingsPresenter = new SettingsPresenter(getActivity());

        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preferences);

        // Get a reference to the preferences.
        weight = (ListPreference) findPreference(KEY_WEIGHT);
        height = (ListPreference) findPreference(KEY_HEIGHT);
        energy = (ListPreference) findPreference(KEY_ENERGY);
        liquid = (ListPreference) findPreference(KEY_LIQUID);
/*        weekday = (ListPreference) findPreference(KEY_WEEKDAY);*/
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
                String wUnits = sharedPreferences.getString(key, "");
                settingsPresenter.updateProfileWeight(wUnits);
                weight.setSummary(wUnits);
                break;

            case KEY_HEIGHT:
                String hUnits = sharedPreferences.getString(key, "");
                settingsPresenter.updateProfileHeight(hUnits);
                height.setSummary(hUnits);
                break;

            case KEY_ENERGY:
                String eUnits = sharedPreferences.getString(key, "");
                settingsPresenter.updateProfileEnergyNeed(eUnits);
                energy.setSummary(sharedPreferences.getString(key, ""));
                break;

            case KEY_LIQUID:
                liquid.setSummary(sharedPreferences.getString(key, ""));
                break;

/*            case KEY_WEEKDAY:
                weekday.setSummary(sharedPreferences.getString(key, ""));
                break;*/

            default: break;
        }
    }
}
