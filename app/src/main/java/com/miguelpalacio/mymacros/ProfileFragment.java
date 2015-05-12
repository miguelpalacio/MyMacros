package com.miguelpalacio.mymacros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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
    public static final String KEY_BMR = "profile_bmr";

    public static final String KEY_ACTIVITY_LEVEL = "profile_activity_level";
    public static final String KEY_GOAL = "profile_goal";
    public static final String KEY_CALORIE_NEED = "profile_daily_calorie_need";

    private static final String defaultSummary1 = "Tap to set";
    private static final String defaultSummary2 = "Waiting for data";

    private SharedPreferences sharedPref;

    private PreferenceCategory yourData;
    private ListPreference gender;
    private EditTextPreference age;
    private EditTextPreference height;
    private TwoInputPreference heightEng;
    private EditTextPreference weight;
    private Preference bmr;

    private ListPreference activityLevel;
    private ListPreference goal;
    private Preference calorieNeed;

    String unitsHeight;
    String unitsWeight;
    String unitsEnergy;

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
        bmr = findPreference(KEY_BMR);

        // Calorie needs.
        activityLevel = (ListPreference) findPreference(KEY_ACTIVITY_LEVEL);
        goal = (ListPreference) findPreference(KEY_GOAL);
        calorieNeed = findPreference(KEY_CALORIE_NEED);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load current measurement units.
        unitsHeight = sharedPref.getString(SettingsFragment.KEY_HEIGHT, "");
        unitsWeight = sharedPref.getString(SettingsFragment.KEY_WEIGHT, "");
        unitsEnergy = sharedPref.getString(SettingsFragment.KEY_ENERGY, "");

        // Set dialog title for items that depend on units.
        height.setDialogTitle("Height (" + unitsHeight + ")");
        weight.setDialogTitle("Weight (" + unitsWeight + ")");

        // Set up items' summary.
        setListPrefSummary(gender, KEY_GENDER, "ND");
        setAgeSummary();
        setHeightSummary();
        setWeightSummary();
        setBmrSummary();

        setListPrefSummary(activityLevel, KEY_ACTIVITY_LEVEL, "ND");
        setListPrefSummary(goal, KEY_GOAL, "ND");

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
        switch (key) {

            case KEY_GENDER:
                setListPrefSummary(gender, KEY_GENDER, "ND");
                setBmrSummary();
                break;

            case KEY_AGE:
                setAgeSummary();
                setBmrSummary();
                break;

            case KEY_HEIGHT:
                setHeightSummary();
                setBmrSummary();
                break;

            case KEY_HEIGHT_ENG:
                setHeightSummary();
                setBmrSummary();
                break;

            case KEY_WEIGHT:
                setWeightSummary();
                setBmrSummary();
                // TODO: store weight changes.
                break;

            case KEY_ACTIVITY_LEVEL:
                setListPrefSummary(activityLevel, KEY_ACTIVITY_LEVEL, "ND");
                setBmrSummary();
                break;

            case KEY_GOAL:
                setListPrefSummary(goal, KEY_GOAL, "ND");
                setBmrSummary();
                break;

        }
    }

    // Set summary for a generic ListPreference.
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

    private void setHeightSummary() {

        if (unitsHeight.equals("cm")) {
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
            String value = heightEng.getValue();
            if (value.equals("0-0")) {
                heightEng.setSummary(defaultSummary1);
            } else {
                String[] values = value.split("-");
                heightEng.setSummary(values[0] + " ft, " + values[1] + " in");
            }
        }
    }

    private void setWeightSummary() {

        if (weight.getText().equals("0")) {
            weight.setSummary(defaultSummary1);
            return;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        double w = Double.parseDouble(getPreferenceScreen().getSharedPreferences().getString(KEY_WEIGHT, ""));

        if (unitsWeight.equals("kg")) {
            weight.setSummary(decimalFormat.format(w) + " kg");
        } else {
            weight.setSummary(decimalFormat.format(w) + " lb");
        }
    }

    private void setBmrSummary() {

        // If there's missing data, set the default summary.
        if (gender.getValue().equals("ND") || age.getText().equals("0") ||
                (height.getText().equals("0") &&  heightEng.getValue().equals("0-0")) ||
                weight.getText().equals("0")) {
            bmr.setSummary(defaultSummary2);
            return;
        }

        int a;
        double h;
        double w;
        double BMR;

        // Get age.
        a = Integer.parseInt(age.getText());

        // Get height and convert it (if necessary) into cm.
        if (unitsHeight.equals("cm")) {
            h = Double.parseDouble(sharedPref.getString(KEY_HEIGHT, ""));
        } else {
            String[] values = sharedPref.getString(KEY_HEIGHT_ENG, "").split("-");
            h = (Double.parseDouble(values[0])*12.0+Double.parseDouble(values[1]))/0.39370;
        }

        // Get weight and convert it (if necessary) into kg.
        if (unitsWeight.equals("kg")) {
            w = Double.parseDouble(sharedPref.getString(KEY_WEIGHT, ""));
        } else {
            w = (Double.parseDouble(sharedPref.getString(KEY_WEIGHT, "")))/2.2046;
        }

        // See formula for BMR: http://www.bmi-calculator.net/bmr-calculator/bmr-formula.php
        // Calculate BMR for women.
        if (gender.getValue().equals("Female")) {
            BMR = 655 + 9.6*w + 1.8*h - 4.7*a;
        }
        // Calculate BMR for men.
        else {
            BMR = 66 + 13.7*w + 5*h - 6.8*a;
        }

        // Set summary.
        if (unitsEnergy.equals("kJ")) {
            BMR = BMR * 4.184;
        }
        bmr.setSummary((int) BMR + " " + unitsEnergy);

        // Set Daily Calorie Need summary.
        setCalorieNeedSummary(BMR);


/*        SharedPreferences.Editor hEditor = sharedPref.edit();
        hEditor.putString(KEY_BMR, "" + (int) BMR);*/
    }

    private void setCalorieNeedSummary(double BMR) {

        // If there's missing data, set the default summary.
        if (activityLevel.getValue().equals("ND") || goal.equals("ND")) {
            calorieNeed.setSummary(defaultSummary2);
            return;
        }

        double TDEE;

        // See formula for TDEE: http://www.bmi-calculator.net/bmr-calculator/harris-benedict-equation/
        switch (activityLevel.getValue()) {

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

        // Calculate calorie intake according to goal.
        if (goal.getValue().equals("Fat Loss")) {
            TDEE = TDEE * 0.8;
        } else if (goal.getValue().equals("Bulking")) {
            TDEE = TDEE * 1.2;
        }

        // Set value in local variable.


        // Set summary.
        calorieNeed.setSummary((int) TDEE + " " + unitsEnergy);
    }
}
