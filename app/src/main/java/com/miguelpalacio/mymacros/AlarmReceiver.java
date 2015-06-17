package com.miguelpalacio.mymacros;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelpalacio.mymacros.database.DatabaseAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * When this receiver is triggered, store some user data into the database.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "Hey, I was executed!", Toast.LENGTH_SHORT).show();

        // Log some user data into the database.
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);

        // Load the data from SharedPreferences.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

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

        // Insert the data into the DailyLogs table of the database.
        databaseAdapter.insertLog(proteinTarget, proteinConsumed, carbsTarget, carbsConsumed,
                fatTarget, fatConsumed, userWeight, logDateTime);
    }
}
