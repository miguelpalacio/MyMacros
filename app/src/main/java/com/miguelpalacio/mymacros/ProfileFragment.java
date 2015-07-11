package com.miguelpalacio.mymacros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.miguelpalacio.mymacros.models.NutritionFormulae;
import com.miguelpalacio.mymacros.models.UnitsConversion;

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
    private Preference energyNeed;

    private SingleLineTextPreference proteinRate;
    private SingleLineTextPreference carbsRate;
    private SingleLineTextPreference fatRate;

    private SingleLinePreference proteinGrams;
    private SingleLinePreference carbosGrams;
    private SingleLinePreference fatGrams;

    private SingleLinePreference fiber;
    private SingleLinePreference water;

    String unitsHeight;
    String unitsWeight;
    String unitsEnergy;
    String unitsLiquid;

    boolean goalChanged;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.#");

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
        energyNeed = findPreference(KEY_ENERGY_NEED);

        // Macronutrient distribution.
        proteinRate = (SingleLineTextPreference) findPreference(KEY_PROTEIN_RATE);
        carbsRate = (SingleLineTextPreference) findPreference(KEY_CARBS_RATE);
        fatRate = (SingleLineTextPreference) findPreference(KEY_FAT_RATE);

        // Macronutrient intake.
        proteinGrams = (SingleLinePreference) findPreference(KEY_PROTEIN_GRAMS);
        carbosGrams = (SingleLinePreference) findPreference(KEY_CARBS_GRAMS);
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
        //heightEng.setText(sharedPref.getString(KEY_HEIGHT_ENG, "0-0"));
        weight.setText(sharedPref.getString(KEY_WEIGHT, ""));

        // Set up items' summary.
        setListPrefSummary(gender, KEY_GENDER, "ND");
        setAgeSummary();
        setHeightSummary();
        setWeightSummary();
        setBmrSummary();

        setCalorieNeedSummary();

        setListPrefSummary(activityLevel, KEY_ACTIVITY_LEVEL, "ND");
        setListPrefSummary(goal, KEY_GOAL, "ND");

        setMacroRateSummary(proteinRate);
        setMacroRateSummary(carbsRate);
        setMacroRateSummary(fatRate);

        setMacroIntake();
        setFiberIntake();
        setWaterIntake();

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
                setBmrSummary();
                setCalorieNeedSummary();
                setMacroIntake();
                setFiberIntake();
                break;

            case KEY_AGE:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    age.setText("0");
                    editor.putString(key, "0").apply();
                }
                setAgeSummary();
                setBmrSummary();
                setCalorieNeedSummary();
                setMacroIntake();
                setFiberIntake();
                break;

            case KEY_HEIGHT:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    height.setText("0");
                    editor.putString(key, "0").apply();
                }
                setHeightSummary();
                setBmrSummary();
                setCalorieNeedSummary();
                setMacroIntake();
                setFiberIntake();
                break;

            case KEY_HEIGHT_ENG:
                setHeightSummary();
                setBmrSummary();
                setCalorieNeedSummary();
                setMacroIntake();
                setFiberIntake();
                break;

            case KEY_WEIGHT:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    weight.setText("0");
                    editor.putString(key, "0").apply();
                }
                setWeightSummary();
                setBmrSummary();
                setCalorieNeedSummary();
                setMacroIntake();
                setFiberIntake();
                setWaterIntake();
                break;

            case KEY_ACTIVITY_LEVEL:
                setListPrefSummary(activityLevel, KEY_ACTIVITY_LEVEL, "ND");
                setBmrSummary();
                setCalorieNeedSummary();
                setMacroIntake();
                setFiberIntake();
                break;

            case KEY_GOAL:
                setListPrefSummary(goal, KEY_GOAL, "ND");
                goalChanged = true;
                MainActivity activity = (MainActivity) getActivity();
                activity.setDrawerHeaderGoal(goal.getValue());
                setCalorieNeedSummary();
                redistributeMacroRate();
                setMacroIntake();
                setFiberIntake();
                break;

            case KEY_PROTEIN_RATE:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    proteinRate.setText("0");
                    editor.putString(key, "0").apply();
                }
                setMacroRateSummary(proteinRate);
                checkMacroDistribution();
                setMacroIntake();
                break;

            case KEY_CARBS_RATE:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    carbsRate.setText("0");
                    editor.putString(key, "0").apply();
                }
                setMacroRateSummary(carbsRate);
                checkMacroDistribution();
                setMacroIntake();
                break;

            case KEY_FAT_RATE:
                if (sharedPreferences.getString(key, "0").length() == 0) {
                    fatRate.setText("0");
                    editor.putString(key, "0").apply();
                }
                setMacroRateSummary(fatRate);
                checkMacroDistribution();
                setMacroIntake();
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

    /**
     * Define the BMR according to the data input by the user.
     * Dependencies: Gender, Age, Height, Weight.
     */
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
            h = Double.parseDouble(sharedPref.getString(KEY_HEIGHT, "0"));
        } else {
            String[] values = sharedPref.getString(KEY_HEIGHT_ENG, "0-0").split("-");
/*            h = (Double.parseDouble(values[0])*12.0+Double.parseDouble(values[1]))/0.39370;*/
            double ft = Double.parseDouble(values[0]);
            double in = Double.parseDouble(values[1]);
            h = UnitsConversion.convertToCentimeters(ft, in);
        }

        // Get weight and convert it (if necessary) into kg.
        w = Double.parseDouble(sharedPref.getString(KEY_WEIGHT, "0"));
        if (!unitsWeight.equals("kg"))
            w = UnitsConversion.convertToPounds(w);
/*        if (unitsWeight.equals("kg")) {
            w = Double.parseDouble(sharedPref.getString(KEY_WEIGHT, "0"));
        } else {
            w = (Double.parseDouble(sharedPref.getString(KEY_WEIGHT, "")))/2.2046;
        }*/

        // Calculate BMR for women.
        if (gender.getValue().equals("Female")) {
            BMR = NutritionFormulae.getBMR(w, h, a, NutritionFormulae.FEMALE);
        }
        // Calculate BMR for men.
        else {
            BMR = NutritionFormulae.getBMR(w, h, a, NutritionFormulae.MALE);
        }
/*        // See formula for BMR: http://www.bmi-calculator.net/bmr-calculator/bmr-formula.php
        if (gender.getValue().equals("Female")) {
            BMR = 655 + 9.6*w + 1.8*h - 4.7*a;
        }
        // Calculate BMR for men.
        else {
            BMR = 66 + 13.7*w + 5*h - 6.8*a;
        }*/

        // Set summary.
        if (unitsEnergy.equals("kJ")) {
            BMR = UnitsConversion.convertToJoules(BMR);
/*            BMR = BMR * 4.184;*/
        }
        bmr.setSummary((int) BMR + " " + unitsEnergy);

        // Store the bmr preference value.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_BMR, "" + BMR).apply();
    }

    /**
     * Set the Daily Calorie need (TDEE).
     * Dependencies: bmr.
     */
    private void setCalorieNeedSummary() {

        Double BMR = Double.parseDouble(sharedPref.getString(KEY_BMR, "0"));

        // If there's missing data, set the default summary.
        if (activityLevel.getValue().equals("ND") || goal.getValue().equals("ND") || BMR == 0) {
            energyNeed.setSummary(defaultSummary2);
            return;
        }

        double TDEE = NutritionFormulae.getTDEE(BMR, activityLevel.getValue(), goal.getValue());

        // See formula for TDEE: http://www.bmi-calculator.net/bmr-calculator/harris-benedict-equation/
/*        switch (activityLevel.getValue()) {

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

        // Calculate the energy intake according to goal.
        if (goal.getValue().equals("Fat Loss")) {
            TDEE = TDEE * 0.8;
        } else if (goal.getValue().equals("Bulking")) {
            TDEE = TDEE * 1.2;
        }*/

        // Store the bmr preference value.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_ENERGY_NEED, decimalFormat.format(TDEE)).apply();

        // Update Navigation Drawer Header's Today progress label:
        MainActivity activity = (MainActivity) getActivity();
        activity.setDrawerHeaderProgress("");

        // Set summary.
        energyNeed.setSummary((int) TDEE + " " + unitsEnergy);
    }

    private void setMacroRateSummary(EditTextPreference p) {
        p.setSummary(p.getText() + " %");
    }


    private void checkMacroDistribution() {
        // Check that macros weren't change programmatically by setting a new goal.
        if (goalChanged) {
            return;
        }
        // Check that proteinRate + carbsRate + fatRate = 100.
        int p = Integer.parseInt(proteinRate.getText());
        int c = Integer.parseInt(carbsRate.getText());
        int f = Integer.parseInt(fatRate.getText());
        if (NutritionFormulae.isValidMacroDistribution(p, c, f)) {
            Toast.makeText(getActivity(), "Correct distribution", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Macronutrient distribution is not 100 %", Toast.LENGTH_SHORT).show();
        }
/*        if (Integer.parseInt(proteinRate.getText()) + Integer.parseInt(carbsRate.getText()) +
                Integer.parseInt(fatRate.getText()) != 100) {
            Toast.makeText(getActivity(), "Macronutrient distribution is not 100 %", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Correct distribution", Toast.LENGTH_SHORT).show();
        }*/
    }

    /**
     * Set the daily macronutrient intake in grams.
     * Dependencies: energyNeed.
     */
    private void setMacroIntake() {

        double TDEE;
        if (sharedPref.getString(KEY_ENERGY_NEED, "0") != null) {
            TDEE = Double.parseDouble(sharedPref.getString(KEY_ENERGY_NEED, "0"));
        } else {
            return;
        }

        double protein, carbs, fat;

        protein = TDEE * Double.parseDouble(proteinRate.getText()) / 100;
        carbs = TDEE * Double.parseDouble(carbsRate.getText()) / 100;
        fat = TDEE * Double.parseDouble(fatRate.getText()) / 100;

        // Convert to kcal if needed.
        if (unitsEnergy.equals("kJ")) {
            protein = protein / 4.184;
            carbs = carbs / 4.184;
            fat = fat / 4.184;
        }

        // Calculate grams per macro. 1 gram of proteinEditText/carbs = 4 kcal, 1 gram of fatEditText = 9 kcal.
        protein = protein / 4;
        carbs = carbs / 4;
        fat = fat / 9;

        // Store the values in SharedPreferences.

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PROTEIN_GRAMS, decimalFormat.format(protein)).apply();
        editor.putString(KEY_CARBS_GRAMS, decimalFormat.format(carbs)).apply();
        editor.putString(KEY_FAT_GRAMS, decimalFormat.format(fat)).apply();

        // Set the summaries.
        proteinGrams.setSummary((int) protein + " g/day");
        carbosGrams.setSummary((int) carbs + " g/day");
        fatGrams.setSummary((int) fat + " g/day");
    }

    // Called when the goal changes.
    private void redistributeMacroRate() {

        String pRate, cRate, fRate;

        // Recommended macro distribution for each goal.
        if (goal.getValue().equals("Fat Loss")) {
            pRate = "35";
            cRate = "40";
            fRate = "25";
        } else if (goal.getValue().equals("Bulking")) {
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

    /**
     * Set the recommended daily fiberEditText intake.
     * Dependencies: energyNeed.
     */
    private void setFiberIntake() {

        // Calculate fiberEditText intake. Formula taken from:
        // http://healthyeating.sfgate.com/calculate-much-fiber-one-needs-day-4814.html
        double f = Double.parseDouble(sharedPref.getString(KEY_ENERGY_NEED, "0"));
        if (unitsEnergy.equals("kJ")) {
            f = f / 4.184;
        }

        // 14 gr of fiberEditText for each 1000 kcal consumed.
        f = f * 14 / 1000;

        // Store value in SharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_FIBER, "" + (int) f).apply();

        // Set Fiber summary.
        fiber.setSummary((int) f + " g/day");
    }

    /**
     * Set the recommended daily water intake.
     * Dependencies: Weight.
     */
    private void setWaterIntake() {

        // Calculate water intake. Formula taken from:
        // http://www.myfooddiary.com/resources/ask_the_expert/recommended_daily_water_intake.asp
        double w = Double.parseDouble(sharedPref.getString(KEY_WEIGHT, ""));
        if (unitsWeight.equals("kg")) {
            w = w * 2.2046;
        }

        // Get water intake in ounces.
        w = w * 0.5;

        // If units = ml, convert it.
        if (unitsLiquid.equals("ml")) {
            w = w / 0.033814;
        }

        // Store value in SharedPreferences.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_WATER, "" + (int) w).apply();

        // Set summary.
        water.setSummary((int) w + " " + unitsLiquid + "/day");
    }
}
