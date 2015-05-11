package com.miguelpalacio.mymacros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.text.DecimalFormat;

/**
 * @author Miguel Palacio
 * <p>
 *     This Fragment extends from PreferenceFragment since it uses the SharedPreferences
 *     object in order to store user's data.
 * </p>
 */
public class ProfileFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_YOUR_DATA = "profile_your_data";
    public static final String KEY_GENDER = "profile_gender";
    public static final String KEY_AGE = "profile_age";
    public static final String KEY_HEIGHT = "profile_height";
    public static final String KEY_HEIGHT_ENG = "profile_height_eng";
    public static final String KEY_WEIGHT = "profile_weight";
    public static final String KEY_ACTIVITY_LEVEL = "profile_activity_level";
    public static final String KEY_BMR = "profile_bmr";

    private static final String defaultSummary1 = "Tap to set";
    private static final String defaultSummary2 = "Waiting for data";

    private SharedPreferences sharedPref;

    private PreferenceCategory yourData;
    private ListPreference gender;
    private EditTextPreference age;
    private EditTextPreference height;
    private TwoInputPreference heightEng;
    private EditTextPreference weight;
    private ListPreference activityLevel;
    private Preference bmr;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the global SharedPreferences file.
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Load the profile data from an XML resource.
        addPreferencesFromResource(R.xml.fragment_profile);

        // Get references to the profile data.

        // Your Data.
        yourData = (PreferenceCategory) findPreference(KEY_YOUR_DATA);
        gender = (ListPreference) findPreference(KEY_GENDER);
        age = (EditTextPreference) findPreference(KEY_AGE);
        height = (EditTextPreference) findPreference(KEY_HEIGHT);
        heightEng = (TwoInputPreference) findPreference(KEY_HEIGHT_ENG);
        weight = (EditTextPreference) findPreference(KEY_WEIGHT);
        activityLevel = (ListPreference) findPreference(KEY_ACTIVITY_LEVEL);
        bmr = findPreference(KEY_BMR);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up the initial summary for the items.

        setPreferenceSummary(gender, KEY_GENDER, defaultSummary1, "ND");
        //setPreferenceSummary(age, KEY_AGE, defaultSummary1, "0");

        setAgeSummary();
        setHeightSummary();
        setWeightSummary();

        setPreferenceSummary(activityLevel, KEY_ACTIVITY_LEVEL, defaultSummary1, "ND");
        setPreferenceSummary(bmr, KEY_BMR, defaultSummary2, "");

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
            setWeightSummary();
        }
    }

    /**
     * Sets the summary for the given preference.
     *
     * @param pref The preference whose summary will be set.
     * @param key The reference to the preference.
     * @param summary A string with the summary to be set.
     * @param defVal A string with the default value of the preference.
     */
    private void setPreferenceSummary(Preference pref, String key, String summary, String defVal) {

        if (pref instanceof EditTextPreference) {
            EditTextPreference p = (EditTextPreference) pref;
            if (p.getText().equals(defVal)) {
                p.setSummary(summary);
            } else {
                p.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
            }
        } else if (pref instanceof ListPreference) {
            ListPreference p = (ListPreference) pref;
            if (p.getValue().equals(defVal)) {
                p.setSummary(summary);
            } else {
                p.setSummary(getPreferenceScreen().getSharedPreferences().getString(key, ""));
            }
        } else {
            pref.setSummary(summary);
        }
    }

    private void setListPrefSummary(ListPreference p, String key, String defVal) {
        if (p.getValue().equals(defVal)) {
            p.setSummary(defaultSummary1);
        } else {
            p.setSummary(sharedPref.getString(key, ""));
        }
    }

    private void setAgeSummary() {
        if (age.getText().equals("0")) {
            age.setSummary(defaultSummary1);
        } else {
            age.setSummary(sharedPref.getString(KEY_AGE, ""));
        }
    }

    private void setWeightSummary() {

        if (weight.getText().equals("0")) {
            weight.setSummary(defaultSummary1);
            return;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        double w = Double.parseDouble(getPreferenceScreen().getSharedPreferences().getString(KEY_WEIGHT, ""));
        String units = sharedPref.getString(SettingsFragment.KEY_WEIGHT, "");

        if (units.equals("lb")) {
            weight.setSummary(decimalFormat.format(w) + " lb");
        } else {
            weight.setSummary(decimalFormat.format(w) + " kg");
        }
    }

    private void setHeightSummary() {

        String units = sharedPref.getString(SettingsFragment.KEY_HEIGHT, "");

        if (units.equals("cm")) {
            yourData.addPreference(height);
            yourData.removePreference(heightEng);
            if (height.getText().equals("0")) {
                height.setSummary(defaultSummary1);
            } else {
                height.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_HEIGHT, "") + " cm");
            }
        } else {
            yourData.addPreference(heightEng);
            yourData.removePreference(height);
            String value = sharedPref.getString(KEY_HEIGHT_ENG, "");
/*            if (heightEng.getValue().equals("0-0")) {*/
            if (value.equals("0-0")) {
                heightEng.setSummary(defaultSummary1);
            } else {
                String[] values = value.split("-");
                heightEng.setSummary(values[0] + " ft, " + values[1] + " in");
            }
        }
    }

    private void calculateBMR() {

        // Check for missing data.
        if (gender.getValue().equals("ND") || age.getText().equals("0") || height.getText().equals("0") ||
                weight.getText().equals("0") || activityLevel.getValue().equals("ND")) {
            return;
        }
    }
}
