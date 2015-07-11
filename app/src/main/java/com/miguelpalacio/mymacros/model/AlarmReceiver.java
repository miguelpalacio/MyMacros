package com.miguelpalacio.mymacros.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelpalacio.mymacros.helpers.Utilities;
import com.miguelpalacio.mymacros.views.PlannerFragment;
import com.miguelpalacio.mymacros.views.ProfileFragment;
import com.miguelpalacio.mymacros.views.SettingsFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * When this receiver is triggered, store some user data into the database.
 */
public class AlarmReceiver extends BroadcastReceiver {

    static String KEY_LAST_DATE_ALARM_TRIGGERED = "lastDateAlarmTriggered";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Load the data from SharedPreferences.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // This alarm may be triggered more than once a day; in order to avoid this behavior,
        // register the last time it went off and check that it was a at least a day before.

        long lastDateTriggered = prefs.getLong(KEY_LAST_DATE_ALARM_TRIGGERED, 0);

        // Get calendar (Date only).
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If last time the alarm went off, wasn't at least a day before, abort.
        if (calendar.getTimeInMillis() <= lastDateTriggered) {
/*            Log.d("Time registered:", "" + lastDateTriggered);
            Log.d("Time Calendar:", "" + calendar.getTimeInMillis());*/
            return;
        }

        // Store the date the alarm went off for further comparisons.
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_LAST_DATE_ALARM_TRIGGERED, calendar.getTimeInMillis()).apply();

        // Log some user data into the database.
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Double>>(){}.getType();
        String json;
        ArrayList<Double> macroList;

        double proteinConsumed;
        double carbsConsumed;
        double fatConsumed;

        json = prefs.getString(PlannerFragment.KEY_MEAL_PROTEIN_LIST, null);

        // If the preference exist, calculate the macro consumption.
        if (json != null) {

            macroList = gson.fromJson(json, type);
            proteinConsumed = Utilities.getSummation(macroList);

            json = prefs.getString(PlannerFragment.KEY_MEAL_CARBS_LIST, null);
            macroList = gson.fromJson(json, type);
            carbsConsumed = Utilities.getSummation(macroList);

            json = prefs.getString(PlannerFragment.KEY_MEAL_FAT_LIST, null);
            macroList = gson.fromJson(json, type);
            fatConsumed = Utilities.getSummation(macroList);

            // Since the day is over, reset all the planner's lists in SharedPreferences.
            editor.putBoolean(PlannerFragment.KEY_RESET_LISTS, true).apply();

        } else {
            proteinConsumed = 0;
            carbsConsumed = 0;
            fatConsumed = 0;
        }

        double proteinTarget = Double.parseDouble(prefs.getString(ProfileFragment.KEY_PROTEIN_GRAMS, "0"));
        double carbsTarget = Double.parseDouble(prefs.getString(ProfileFragment.KEY_CARBS_GRAMS, "0"));
        double fatTarget = Double.parseDouble(prefs.getString(ProfileFragment.KEY_FAT_GRAMS, "0"));

        double userWeight = Double.parseDouble(prefs.getString(ProfileFragment.KEY_WEIGHT, "0"));
        if (prefs.getString(SettingsFragment.KEY_WEIGHT, "").equals("lb")) {
            // Always store weight in kg.
            userWeight = userWeight / 2.2046;
        }

        long logDateTime = System.currentTimeMillis();

/*        Toast.makeText(context, "Alarm went off!!!", Toast.LENGTH_SHORT).show();*/

        // Insert the data into the DailyLogs table of the database.
        databaseAdapter.insertLog(proteinTarget, proteinConsumed, carbsTarget, carbsConsumed,
                fatTarget, fatConsumed, userWeight, logDateTime);
    }
}
