package com.miguelpalacio.mymacros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.miguelpalacio.mymacros.presenters.ProfilePresenter;

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
    public static final String KEY_ENERGY_NEED = "profile_daily_energy_need";

    public static final String KEY_PROTEIN_RATE = "profile_protein_rate";
    public static final String KEY_CARBS_RATE = "profile_carbohydrates_rate";
    public static final String KEY_FAT_RATE = "profile_fat_rate";

    public static final String KEY_PROTEIN_GRAMS = "profile_protein_grams";
    public static final String KEY_CARBS_GRAMS = "profile_carbohydrates_grams";
    public static final String KEY_FAT_GRAMS = "profile_fat_grams";

    public static final String KEY_FIBER = "profile_fiber";
    public static final String KEY_WATER = "profile_water";

    private static final String defaultSummary1 = "Tap to set";

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
    private Preference energyNeed;

    private SingleLineTextPreference proteinRate;
    private SingleLineTextPreference carbsRate;
    private SingleLineTextPreference fatRate;

    private SingleLinePreference proteinGrams;
    private SingleLinePreference carbsGrams;
    private SingleLinePreference fatGrams;

    private SingleLinePreference fiber;
    private SingleLinePreference water;

    String unitsHeight;
    String unitsWeight;
    String unitsEnergy;
    String unitsLiquid;

    boolean goalChanged;

    ProfilePresenter profilePresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate a profilePresenter for this view.
        profilePresenter = new ProfilePresenter(getActivity());

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
        energyNeed = findPreference(KEY_ENERGY_NEED);

        // Macronutrient distribution.
        proteinRate = (SingleLineTextPreference) findPreference(KEY_PROTEIN_RATE);
        carbsRate = (SingleLineTextPreference) findPreference(KEY_CARBS_RATE);
        fatRate = (SingleLineTextPreference) findPreference(KEY_FAT_RATE);

        // Macronutrient intake.
        proteinGrams = (SingleLinePreference) findPreference(KEY_PROTEIN_GRAMS);
        carbsGrams = (SingleLinePreference) findPreference(KEY_CARBS_GRAMS);
        fatGrams = (SingleLinePreference) findPreference(KEY_FAT_GRAMS);

        // Recommended intakes.
        fiber = (SingleLinePreference) findPreference(KEY_FIBER);
        water = (SingleLinePreference) findPreference(KEY_WATER);

        // Set up listeners on macro rate preferences to enable checkMacroDistribution().
        proteinRate.setOnPreferenceClickListener(new PreferenceCategory.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                goalChanged = false;
                return false;
            }
        });
        carbsRate.setOnPreferenceClickListener(new PreferenceCategory.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                goalChanged = false;
                return false;
            }
        });
        fatRate.setOnPreferenceClickListener(new PreferenceCategory.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                goalChanged = false;
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load current measurement units.
        unitsHeight = sharedPref.getString(SettingsFragment.KEY_HEIGHT, "");
        unitsWeight = sharedPref.getString(SettingsFragment.KEY_WEIGHT, "");
        unitsEnergy = sharedPref.getString(SettingsFragment.KEY_ENERGY, "");
        unitsLiquid = sharedPref.getString(SettingsFragment.KEY_LIQUID, "");

        // Set dialog title for items that depend on units.
        height.setDialogTitle("Height (" + unitsHeight + ")");
        weight.setDialogTitle("Weight (" + unitsWeight + ")");

        // Set text for Height and Weight in case of change of units in SettingsActivity.
        height.setText(sharedPref.getString(KEY_HEIGHT, ""));
        weight.setText(sharedPref.getString(KEY_WEIGHT, ""));

        // Set up items' summary.
        setListPrefSummary(gender, KEY_GENDER, "ND");
        setAgeSummary();
        setHeightSummary();
        setWeightSummary();
        bmr.setSummary(profilePresenter.getBmrSummary());

        energyNeed.setSummary(profilePresenter.getEnergyNeedSummary());

        setListPrefSummary(activityLevel, KEY_ACTIVITY_LEVEL, "ND");
        setListPrefSummary(goal, KEY_GOAL, "ND");

        setMacroRateSummary(proteinRate);
        setMacroRateSummary(carbsRate);
        setMacroRateSummary(fatRate);

        profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
        fiber.setSummary(profilePresenter.getFiberSummary());
        water.setSummary(profilePresenter.getWaterSummary());

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

        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (key) {

            case KEY_GENDER:
                setListPrefSummary(gender, KEY_GENDER, "ND");
                bmr.setSummary(profilePresenter.getBmrSummary());
                energyNeed.setSummary(profilePresenter.getEnergyNeedSummary());
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
                fiber.setSummary(profilePresenter.getFiberSummary());
                break;

            case KEY_AGE:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    age.setText("0");
                    editor.putString(key, "0").apply();
                }
                setAgeSummary();
                bmr.setSummary(profilePresenter.getBmrSummary());
                energyNeed.setSummary(profilePresenter.getEnergyNeedSummary());
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
                fiber.setSummary(profilePresenter.getFiberSummary());
                break;

            case KEY_HEIGHT:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    height.setText("0");
                    editor.putString(key, "0").apply();
                }
                setHeightSummary();
                bmr.setSummary(profilePresenter.getBmrSummary());
                energyNeed.setSummary(profilePresenter.getEnergyNeedSummary());
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
                fiber.setSummary(profilePresenter.getFiberSummary());
                break;

            case KEY_HEIGHT_ENG:
                setHeightSummary();
                bmr.setSummary(profilePresenter.getBmrSummary());
                energyNeed.setSummary(profilePresenter.getEnergyNeedSummary());
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
                fiber.setSummary(profilePresenter.getFiberSummary());
                break;

            case KEY_WEIGHT:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    weight.setText("0");
                    editor.putString(key, "0").apply();
                }
                setWeightSummary();
                bmr.setSummary(profilePresenter.getBmrSummary());
                energyNeed.setSummary(profilePresenter.getEnergyNeedSummary());
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
                fiber.setSummary(profilePresenter.getFiberSummary());
                water.setSummary(profilePresenter.getWaterSummary());
                break;

            case KEY_ACTIVITY_LEVEL:
                setListPrefSummary(activityLevel, KEY_ACTIVITY_LEVEL, "ND");
                bmr.setSummary(profilePresenter.getBmrSummary());
                energyNeed.setSummary(profilePresenter.getEnergyNeedSummary());
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
                fiber.setSummary(profilePresenter.getFiberSummary());
                break;

            case KEY_GOAL:
                setListPrefSummary(goal, KEY_GOAL, "ND");
                goalChanged = true;
                MainActivity activity = (MainActivity) getActivity();
                activity.setDrawerHeaderGoal(goal.getValue());
                energyNeed.setSummary(profilePresenter.getEnergyNeedSummary());
                profilePresenter.redistributeMacroRate(proteinRate, carbsRate, fatRate);
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
                fiber.setSummary(profilePresenter.getFiberSummary());
                break;

            case KEY_PROTEIN_RATE:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    proteinRate.setText("0");
                    editor.putString(key, "0").apply();
                }
                setMacroRateSummary(proteinRate);
                profilePresenter.checkMacroDistribution(goalChanged);
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
                break;

            case KEY_CARBS_RATE:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    carbsRate.setText("0");
                    editor.putString(key, "0").apply();
                }
                setMacroRateSummary(carbsRate);
                profilePresenter.checkMacroDistribution(goalChanged);
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
                break;

            case KEY_FAT_RATE:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    fatRate.setText("0");
                    editor.putString(key, "0").apply();
                }
                setMacroRateSummary(fatRate);
                profilePresenter.checkMacroDistribution(goalChanged);
                profilePresenter.setMacroIntakeSummaries(proteinGrams, carbsGrams, fatGrams);
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
                height.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_HEIGHT, "0") + " cm");
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
        double w = Double.parseDouble(getPreferenceScreen().getSharedPreferences().getString(KEY_WEIGHT, "0"));

        if (unitsWeight.equals("kg")) {
            weight.setSummary(decimalFormat.format(w) + " kg");
        } else {
            weight.setSummary(decimalFormat.format(w) + " lb");
        }
    }

    private void setMacroRateSummary(EditTextPreference p) {
        p.setSummary(p.getText() + " %");
    }

}
