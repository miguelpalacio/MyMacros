package com.miguelpalacio.mymacros.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.miguelpalacio.mymacros.Food;
import com.miguelpalacio.mymacros.Meal;
import com.miguelpalacio.mymacros.MealFood;
import com.miguelpalacio.mymacros.Utilities;
import com.miguelpalacio.mymacros.database.datatypes.MacrosConsumed;
import com.miguelpalacio.mymacros.database.datatypes.WeeklyConsumption;
import com.miguelpalacio.mymacros.database.datatypes.WeightLogs;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

/**
 * This class handles everything related with the application database.
 */
public class DatabaseAdapter {

    DatabaseHelper helper;

    public DatabaseAdapter(Context context) {
        helper = new DatabaseHelper(context);
    }


    // *********************************************************************************************
    // Methods to perform operations in the Foods table.

    // Insert a tuple into the Foods table.
    public long insertFood(String name, double portionQuantity, String portionUnits,
                           double proteinQuantity, double carbosQuantity,
                           double fatQuantity, double fiberQuantity) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.PORTION_QUANTITY, portionQuantity);
        contentValues.put(DatabaseHelper.PORTION_UNITS, portionUnits);
        contentValues.put(DatabaseHelper.PROTEIN, proteinQuantity);
        contentValues.put(DatabaseHelper.CARBS, carbosQuantity);
        contentValues.put(DatabaseHelper.FAT, fatQuantity);
        contentValues.put(DatabaseHelper.FIBER, fiberQuantity);

        return db.insert(DatabaseHelper.TABLE_FOODS, null, contentValues);
    }

    /**
     * Gets information for a given Food ID.
     * @return an object of class Food containing all the info of the food corresponding to that ID.
     */
    public Food getFood(long foodId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String column = "*";
        String[] columns = {column};
        String selection = DatabaseHelper.FOOD_ID + " = ?";
        String[] selectionArgs = {Double.toString(foodId)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_FOODS, columns,
                selection, selectionArgs, null, null, null);

        Food food = new Food();
        food.setId(foodId);

        while (cursor.moveToNext()) {
            int index;

            index = cursor.getColumnIndex(DatabaseHelper.NAME);
            food.setName(cursor.getString(index));

            index = cursor.getColumnIndex(DatabaseHelper.PORTION_QUANTITY);
            food.setPortionQuantity(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.PORTION_UNITS);
            food.setPortionUnits(cursor.getString(index));

            index = cursor.getColumnIndex(DatabaseHelper.PROTEIN);
            food.setProtein(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.CARBS);
            food.setCarbs(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FAT);
            food.setFat(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FIBER);
            food.setFiber(cursor.getDouble(index));
        }
        cursor.close();

        return food;
    }

    // Update a tuple from the Foods table given an ID and the updated data.
    public int updateFood(long foodId, String name, double portionQuantity, String portionUnits,
                          double proteinQuantity, double carbosQuantity,
                          double fatQuantity, double fiberQuantity) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.PORTION_QUANTITY, portionQuantity);
        contentValues.put(DatabaseHelper.PORTION_UNITS, portionUnits);
        contentValues.put(DatabaseHelper.PROTEIN, proteinQuantity);
        contentValues.put(DatabaseHelper.CARBS, carbosQuantity);
        contentValues.put(DatabaseHelper.FAT, fatQuantity);
        contentValues.put(DatabaseHelper.FIBER, fiberQuantity);

        String whereClause = DatabaseHelper.FOOD_ID + " = ?";
        String[] whereArgs = {Long.toString(foodId)};

        return db.update(DatabaseHelper.TABLE_FOODS, contentValues, whereClause, whereArgs);
    }

    // Delete a tuple from the Foods table.
    public int deleteFood(long foodId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = DatabaseHelper.FOOD_ID + " = ?";
        String[] whereArgs = {Long.toString(foodId)};

        int foodDeleted;
        boolean transactionSuccessful = true;

        // Perform the deletions on the Meals and MealFoods tables by means of a transaction.
        db.beginTransaction();
        try {
            foodDeleted = db.delete(DatabaseHelper.TABLE_FOODS, whereClause, whereArgs);

            // Delete the rows corresponding to food in the MealFoods table.
            int foodsDeleted = db.delete(DatabaseHelper.TABLE_MEAL_FOODS, whereClause, whereArgs);

            if (foodDeleted != 1 || foodsDeleted < 1) {
                transactionSuccessful = false;
            }

            if (transactionSuccessful) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return foodDeleted;
    }

    /**
     * Retrieve the name and a summary for each item in the Foods table.
     * @return five arrays:
     * info[0]: contains the list of food ids.
     * info[1]: contains the list of food names and subheaders for the food list.
     * info[2]: contains the summaries
     * info[3]: it's a "boolean" array expressing which position corresponds to a subheader.
     * info[4]: contains the units used to indicate the portion quantity.
     */
    public String[][] getFoodsList() {
        SQLiteDatabase db = helper.getReadableDatabase();

        // SELECT Name, Protein, Carbohydrates, Fat FROM Foods;
        String[] columns = {DatabaseHelper.FOOD_ID, DatabaseHelper.NAME,
                DatabaseHelper.PROTEIN, DatabaseHelper.CARBS, DatabaseHelper.FAT,
                DatabaseHelper.PORTION_UNITS};
        String orderBy = DatabaseHelper.NAME + " COLLATE LOCALIZED ASC";
        Cursor cursor = db.query(DatabaseHelper.TABLE_FOODS, columns,
        null, null, null, null, orderBy);

        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> summaries = new ArrayList<>();
        List<String> isSubheader = new ArrayList<>();
        List<String> portionUnits = new ArrayList<>();

        Character lastSubheader = '\0';
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        while (cursor.moveToNext()) {
            int index;

            // Get Food information.
            index = cursor.getColumnIndex(DatabaseHelper.FOOD_ID);
            long id = cursor.getLong(index);

            index = cursor.getColumnIndex(DatabaseHelper.NAME);
            String name = cursor.getString(index);

            index = cursor.getColumnIndex(DatabaseHelper.PROTEIN);
            double protein = cursor.getDouble(index);

            index = cursor.getColumnIndex(DatabaseHelper.CARBS);
            double carbs = cursor.getDouble(index);

            index = cursor.getColumnIndex(DatabaseHelper.FAT);
            double fat = cursor.getDouble(index);

            index = cursor.getColumnIndex(DatabaseHelper.PORTION_UNITS);
            String units = cursor.getString(index);

            // Define if a lastSubheader should be placed.
            if (lastSubheader != Utilities.flattenToAscii(name.charAt(0))) {
                // Check for numeric characters and special symbols.
                if (Character.isLetter(name.charAt(0))) {
                    lastSubheader = name.charAt(0);
                    ids.add("-1");
                    names.add(lastSubheader.toString());
                    summaries.add("");
                    isSubheader.add("1");
                    portionUnits.add("");
                }
                // If special character, set subheader as '#' if it hasn't been set.
                else if (lastSubheader != '#') {
                    lastSubheader = '#';
                    ids.add("-1");
                    names.add(lastSubheader.toString());
                    summaries.add("");
                    isSubheader.add("1");
                    portionUnits.add("");
                }
            }
            ids.add("" + id);
            names.add(name);
            isSubheader.add("0");
            portionUnits.add(units);

            // Parse data and place it in summaries.
            String foodSummary = "Protein: " + decimalFormat.format(protein) +
                    " g, Carbohydrates: " + decimalFormat.format(carbs) +
                    " g, Fat: " + decimalFormat.format(fat) + " g";
            summaries.add(foodSummary);
        }
        cursor.close();

        // Store the resulting ArrayLists in a single data structure, and return it.
        String[][] info = new String[5][ids.size()];
        info[0] = ids.toArray(new String[ids.size()]);
        info[1] = names.toArray(new String[ids.size()]);
        info[2] = summaries.toArray(new String[ids.size()]);
        info[3] = isSubheader.toArray(new String[ids.size()]);
        info[4] = portionUnits.toArray(new String[ids.size()]);

        return info;
    }

    /**
     * Check that there is no food tuple with the same name in the Foods table.
     */
    public boolean isNameInFoods(String foodName) {
        SQLiteDatabase db = helper.getReadableDatabase();

        // SELECT COUNT(*) WHERE Name = foodName;
        String column = "COUNT(*)";
        String[] columns = {column};
        String selection = DatabaseHelper.NAME + " = ?";
        String[] selectionArgs = {foodName};
        Cursor cursor = db.query(DatabaseHelper.TABLE_FOODS, columns,
                selection, selectionArgs, null, null, null);

        int numberOfFoods = 0;

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(column);
            numberOfFoods = cursor.getInt(index);
        }
        cursor.close();

        return (numberOfFoods > 0);
    }


    // *********************************************************************************************
    // Methods to perform operations in the Meals and MealFoods tables.

    /**
     * Inserts a new row in the Meals table. Also inserts the information of the foods
     * added to the meal, into the MealFoods table.
     * @return the id given in the database for the new meal.
     */
    public long insertMeal(String name, double proteinQuantity, double carbsQuantity, double fatQuantity,
                           double fiberQuantity, List<String> foodIdList, List<String> foodQuantityList) {
        SQLiteDatabase db = helper.getWritableDatabase();

        long mealId = -1;
        boolean transactionSuccessful = true;

        // Perform the insertions into the Meals and MealFoods tables by means of a transaction.
        db.beginTransaction();
        try {
            // Insert Meal's data into the Meals table.
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.NAME, name);
            contentValues.put(DatabaseHelper.PROTEIN, proteinQuantity);
            contentValues.put(DatabaseHelper.CARBS, carbsQuantity);
            contentValues.put(DatabaseHelper.FAT, fatQuantity);
            contentValues.put(DatabaseHelper.FIBER, fiberQuantity);

            mealId = db.insert(DatabaseHelper.TABLE_MEALS, null, contentValues);

            if (mealId < 0) {
                transactionSuccessful = false;
            }

            // Insert meal foods' info the MealFoods table.
            for (int i = 0; i < foodIdList.size(); i++) {
                contentValues = new ContentValues();
                contentValues.put(DatabaseHelper.MEAL_ID, mealId);
                contentValues.put(DatabaseHelper.FOOD_ID, Long.parseLong(foodIdList.get(i)));
                contentValues.put(DatabaseHelper.FOOD_QUANTITY, Double.parseDouble(foodQuantityList.get(i)));

                long mealFoodId = db.insert(DatabaseHelper.TABLE_MEAL_FOODS, null, contentValues);

                if (mealFoodId < 0) {
                    transactionSuccessful = false;
                    mealId = -1;
                }
            }

            if (transactionSuccessful) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return mealId;
    }

    /**
     * Gets information for a given Meal ID.
     * @return an object of class Meal containing all the info of the meal corresponding to that ID.
     */
    public Meal getMeal(long mealId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String column = "*";
        String[] columns = {column};
        String selection = DatabaseHelper.MEAL_ID + " = ?";
        String[] selectionArgs = {Double.toString(mealId)};

        // Query the Meals table.
        Cursor cursor = db.query(DatabaseHelper.TABLE_MEALS, columns,
                selection, selectionArgs, null, null, null);

        Meal meal = new Meal();
        meal.setId(mealId);

        while (cursor.moveToNext()) {
            int index;

            index = cursor.getColumnIndex(DatabaseHelper.NAME);
            meal.setName(cursor.getString(index));

            index = cursor.getColumnIndex(DatabaseHelper.PROTEIN);
            meal.setProtein(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.CARBS);
            meal.setCarbs(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FAT);
            meal.setFat(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FIBER);
            meal.setFiber(cursor.getDouble(index));
        }
        cursor.close();

        // Query the MealFoods table.
        cursor = db.query(DatabaseHelper.TABLE_MEAL_FOODS, columns,
                selection, selectionArgs, null, null, null);

        List<MealFood> foods = new ArrayList<>();

        while (cursor.moveToNext()) {
            MealFood food;
            int index;

            index = cursor.getColumnIndex(DatabaseHelper.FOOD_ID);
            food = new MealFood(getFood(cursor.getLong(index)));

            index = cursor.getColumnIndex(DatabaseHelper.FOOD_QUANTITY);
            food.setFoodQuantity(cursor.getDouble(index));

            foods.add(food);
        }
        cursor.close();
        meal.setFoods(foods);

        return meal;
    }

    // Update a tuple from the Meals table, and its corresponding ones in MealFoods table.
    public int updateMeal(Long mealId, String name, double proteinQuantity, double carbsQuantity,
                          double fatQuantity, double fiberQuantity, List<Long> deletedFoods,
                          List<Long> newFoods, List<Double> newFoodsQuantities,
                          List<Long> updatedFoods, List<Double> updatedFoodQuantities ) {

        SQLiteDatabase db = helper.getWritableDatabase();

        int updateResult = -1;
        boolean transactionSuccessful = true;

        // Perform the insertions into the Meals and MealFoods tables by means of a transaction.
        db.beginTransaction();
        try {
            // Update Meal's data in Meals table.
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.NAME, name);
            contentValues.put(DatabaseHelper.PROTEIN, proteinQuantity);
            contentValues.put(DatabaseHelper.CARBS, carbsQuantity);
            contentValues.put(DatabaseHelper.FAT, fatQuantity);
            contentValues.put(DatabaseHelper.FIBER, fiberQuantity);

            String whereClause = DatabaseHelper.MEAL_ID + " = ?";
            String[] whereArgs = {Long.toString(mealId)};

            updateResult = db.update(DatabaseHelper.TABLE_MEALS, contentValues, whereClause, whereArgs);

            if (updateResult != 1) {
                transactionSuccessful = false;
            }

            // Delete in MealFoods table, the foods that were deleted from meal.
            whereClause = DatabaseHelper.MEAL_ID + " = ? AND " + DatabaseHelper.FOOD_ID + " = ?";

            for (int i = 0; i < deletedFoods.size(); i++) {
                whereArgs = new String[]{Long.toString(mealId), Long.toString(deletedFoods.get(i))};
                int result = db.delete(DatabaseHelper.TABLE_MEAL_FOODS, whereClause, whereArgs);
                if (result != 1) {
                    transactionSuccessful = false;
                }
            }

            // Insert in MealFoods table, the foods that were added to meal.
            for (int i = 0; i < newFoods.size(); i++) {
                contentValues = new ContentValues();
                contentValues.put(DatabaseHelper.MEAL_ID, mealId);
                contentValues.put(DatabaseHelper.FOOD_ID, newFoods.get(i));
                contentValues.put(DatabaseHelper.FOOD_QUANTITY, newFoodsQuantities.get(i));

                long mealFoodId = db.insert(DatabaseHelper.TABLE_MEAL_FOODS, null, contentValues);
                if (mealFoodId < 0) {
                    transactionSuccessful = false;
                }
            }

            // Update in MealFoods table, the foods that were updated in meal.
            whereClause = DatabaseHelper.MEAL_ID + " = ? AND " + DatabaseHelper.FOOD_ID + " = ?";

            for (int i = 0; i < updatedFoods.size(); i++) {
                whereArgs = new String[]{Long.toString(mealId), Long.toString(updatedFoods.get(i))};
                contentValues = new ContentValues();
                contentValues.put(DatabaseHelper.FOOD_QUANTITY, updatedFoodQuantities.get(i));

                int result = db.update(DatabaseHelper.TABLE_MEAL_FOODS, contentValues,
                        whereClause, whereArgs);
                if (result != 1) {
                    transactionSuccessful = false;
                }
            }

            if (transactionSuccessful) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return updateResult;
    }

    // Delete a tuple from the Meals table, and its corresponding ones in MealFoods table.
    public int deleteMeal(long mealId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = DatabaseHelper.MEAL_ID + " = ?";
        String[] whereArgs = {Long.toString(mealId)};

        int mealDeleted;
        boolean transactionSuccessful = true;

        // Perform the deletions on the Meals and MealFoods tables by means of a transaction.
        db.beginTransaction();
        try {
            mealDeleted = db.delete(DatabaseHelper.TABLE_MEALS, whereClause, whereArgs);

            // Delete the foods corresponding to meal in the MealFoods table.
            int foodsDeleted = db.delete(DatabaseHelper.TABLE_MEAL_FOODS, whereClause, whereArgs);

            if (mealDeleted != 1 || foodsDeleted < 1) {
                transactionSuccessful = false;
            }

            if (transactionSuccessful) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return mealDeleted;
    }

    /**
     * Retrieve the name and a summary for each item in the Meals table.
     * @return four arrays:
     * info[0]: contains the list of meal ids.
     * info[1]: contains the list of meal names and subheaders for the meal list.
     * info[2]: contains the summaries.
     * info[3]: it's a "boolean" array expressing which position corresponds to a subheader.
     */
    public String[][] getMealsList() {
        SQLiteDatabase db = helper.getReadableDatabase();

        // SELECT Name, Protein, Carbohydrates, Fat FROM Foods;
        String[] columns = {DatabaseHelper.MEAL_ID, DatabaseHelper.NAME,
                DatabaseHelper.PROTEIN, DatabaseHelper.CARBS, DatabaseHelper.FAT};
        String orderBy = DatabaseHelper.NAME + " COLLATE LOCALIZED ASC";
        Cursor cursor = db.query(DatabaseHelper.TABLE_MEALS, columns,
                null, null, null, null, orderBy);

        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> summaries = new ArrayList<>();
        List<String> isSubheader = new ArrayList<>();

        Character lastSubheader = '\0';
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        while (cursor.moveToNext()) {
            int index;

            // Get Food information.
            index = cursor.getColumnIndex(DatabaseHelper.MEAL_ID);
            long id = cursor.getLong(index);

            index = cursor.getColumnIndex(DatabaseHelper.NAME);
            String name = cursor.getString(index);

            index = cursor.getColumnIndex(DatabaseHelper.PROTEIN);
            double protein = cursor.getDouble(index);

            index = cursor.getColumnIndex(DatabaseHelper.CARBS);
            double carbs = cursor.getDouble(index);

            index = cursor.getColumnIndex(DatabaseHelper.FAT);
            double fat = cursor.getDouble(index);

            // Define if a lastSubheader should be placed.
            if (lastSubheader != Utilities.flattenToAscii(name.charAt(0))) {
                // Check for numeric characters and special symbols.
                if (Character.isLetter(name.charAt(0))) {
                    lastSubheader = name.charAt(0);
                    ids.add("-1");
                    names.add(lastSubheader.toString());
                    summaries.add("");
                    isSubheader.add("1");
                }
                // If special character, set subheader as '#' if it hasn't been set.
                else if (lastSubheader != '#') {
                    lastSubheader = '#';
                    ids.add("-1");
                    names.add(lastSubheader.toString());
                    summaries.add("");
                    isSubheader.add("1");
                }
            }
            ids.add("" + id);
            names.add(name);
            isSubheader.add("0");

            // Parse data and place it in summaries.
            String mealSummary = "Protein: " + decimalFormat.format(protein) +
                    " g, Carbohydrates: " + decimalFormat.format(carbs) +
                    " g, Fat: " + decimalFormat.format(fat) + " g";
            summaries.add(mealSummary);
        }
        cursor.close();

        // Store the resulting ArrayLists in a single data structure, and return it.
        String[][] info = new String[5][ids.size()];
        info[0] = ids.toArray(new String[ids.size()]);
        info[1] = names.toArray(new String[ids.size()]);
        info[2] = summaries.toArray(new String[ids.size()]);
        info[3] = isSubheader.toArray(new String[ids.size()]);

        return info;
    }

    /**
     * Check that there is no food tuple with the same name in the Foods table.
     */
    public boolean isNameInMeals(String mealName) {
        SQLiteDatabase db = helper.getReadableDatabase();

        // SELECT COUNT(*) WHERE Name = mealName;
        String column = "COUNT(*)";
        String[] columns = {column};
        String selection = DatabaseHelper.NAME + " = ?";
        String[] selectionArgs = {mealName};
        Cursor cursor = db.query(DatabaseHelper.TABLE_MEALS, columns,
                selection, selectionArgs, null, null, null);

        int numberOfMeals = 0;

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(column);
            numberOfMeals = cursor.getInt(index);
        }
        cursor.close();

        return (numberOfMeals > 0);
    }


    // *********************************************************************************************
    // Methods to perform operations in the Meals and MealFoods tables.

    /**
     * Inserts a new row in the DailyLogs table.
     * @return the id given in the database for the new log.
     */
    public long insertLog(double proteinTarget, double proteinConsumed, double carbsTarget,
                          double carbsConsumed, double fatTarget, double fatConsumed,
                          double userWeight, long logDateTime) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.PROTEIN_TARGET, proteinTarget);
        contentValues.put(DatabaseHelper.PROTEIN_CONSUMED, proteinConsumed);
        contentValues.put(DatabaseHelper.CARBS_TARGET, carbsTarget);
        contentValues.put(DatabaseHelper.CARBS_CONSUMED, carbsConsumed);
        contentValues.put(DatabaseHelper.FAT_TARGET, fatTarget);
        contentValues.put(DatabaseHelper.FAT_CONSUMED, fatConsumed);
        contentValues.put(DatabaseHelper.USER_WEIGHT, userWeight);
        contentValues.put(DatabaseHelper.LOG_DATE_TIME, logDateTime);

        return db.insert(DatabaseHelper.TABLE_DAILY_LOGS, null, contentValues);
    }

    /**
     * Gets the consumption of each macronutrient for the last (given) days.
     */
    public MacrosConsumed getMacrosConsumed(int numberOfLogs) {
        SQLiteDatabase db = helper.getReadableDatabase();

        // SELECT ProteinConsumed, CarbsConsumed, FatConsumed, LogDateTime FROM DailyLogs;
        String[] columns = {DatabaseHelper.PROTEIN_CONSUMED, DatabaseHelper.CARBS_CONSUMED,
                DatabaseHelper.FAT_CONSUMED, DatabaseHelper.LOG_DATE_TIME};
        String orderBy = DatabaseHelper.LOG_DATE_TIME + " DESC";
        Cursor cursor = db.query(DatabaseHelper.TABLE_DAILY_LOGS, columns,
                null, null, null, null, orderBy, "" + numberOfLogs);

        List<Double> proteinConsumed = new ArrayList<>();
        List<Double> carbsConsumed = new ArrayList<>();
        List<Double> fatConsumed = new ArrayList<>();
        List<Long> dateLogs = new ArrayList<>();

        while (cursor.moveToNext()) {
            int index;

            index = cursor.getColumnIndex(DatabaseHelper.PROTEIN_CONSUMED);
            proteinConsumed.add(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.CARBS_CONSUMED);
            carbsConsumed.add(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FAT_CONSUMED);
            fatConsumed.add(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.LOG_DATE_TIME);
            dateLogs.add(cursor.getLong(index));
        }
        cursor.close();

        return new MacrosConsumed(proteinConsumed, carbsConsumed, fatConsumed, dateLogs);
    }

    /**
     * Gets the calorie consumption for the last 4 weeks.
     */
    public WeeklyConsumption getWeeklyConsumption(int numberOfWeeks) {
        SQLiteDatabase db = helper.getReadableDatabase();

        List<Double> caloriesConsumed = new ArrayList<>();
        List<String> weeks = new ArrayList<>();

        for (int i = 0; i < numberOfWeeks; i++) {

            double caloriesWeek = 0;
            boolean thereAreLogs = false;

            // Week goes from Monday at 12:05 am, to next Monday at 12:05 am.

            Calendar weekStart = Calendar.getInstance();
            weekStart.setTimeInMillis(System.currentTimeMillis());
            weekStart.add(Calendar.WEEK_OF_YEAR, -(i + 1));
            weekStart.set(Calendar.DAY_OF_WEEK, 2);
            weekStart.set(Calendar.HOUR_OF_DAY, 0);
            weekStart.set(Calendar.MINUTE, 5);
/*            weekStart.add(Calendar.DAY_OF_WEEK, -(weekStart.get(Calendar.DAY_OF_WEEK)) + 2);
            weekStart.add(Calendar.HOUR_OF_DAY, -(weekStart.get(Calendar.HOUR_OF_DAY)));
            weekStart.add(Calendar.MINUTE, -(weekStart.get(Calendar.MINUTE)));*/

            Calendar weekEnd = Calendar.getInstance();
            weekEnd.setTimeInMillis(System.currentTimeMillis());
            weekEnd.add(Calendar.WEEK_OF_YEAR, -i);
            // TODO: include the "Week Starts On" preference here!
/*            weekEnd.clear(Calendar.DAY_OF_WEEK);
            weekEnd.add(Calendar.DAY_OF_WEEK, 2);*/
            weekEnd.set(Calendar.DAY_OF_WEEK, 2);
            weekEnd.set(Calendar.HOUR_OF_DAY, 0);
            weekEnd.set(Calendar.MINUTE, 5);

            long weekStartInMillis = weekStart.getTimeInMillis();
            long weekEndInMillis = weekEnd.getTimeInMillis();

            // Change day to show correct period of time on label.
            weekEnd.add(Calendar.DAY_OF_WEEK, -1);

            // Define week label.
            String week = weekStart.get(Calendar.DAY_OF_MONTH) + "/" + (1 + weekStart.get(Calendar.MONTH)) +
                    " - " + weekEnd.get(Calendar.DAY_OF_MONTH) + "/" + (1 + weekEnd.get(Calendar.MONTH));

            // SELECT ProteinConsumed, CarbsConsumed, FatConsumed, FROM DailyLogs
            // WHERE logDateTime BETWEEN weekStart AND week;
            String[] columns = {DatabaseHelper.PROTEIN_CONSUMED, DatabaseHelper.CARBS_CONSUMED,
                    DatabaseHelper.FAT_CONSUMED, DatabaseHelper.LOG_DATE_TIME};
            String whereClause = DatabaseHelper.LOG_DATE_TIME + " BETWEEN ? AND ?";
            String[] whereArgs = {Long.toString(weekStartInMillis), Long.toString(weekEndInMillis)};
            String orderBy = DatabaseHelper.LOG_DATE_TIME + " DESC";

            Cursor cursor = db.query(DatabaseHelper.TABLE_DAILY_LOGS, columns,
                    whereClause, whereArgs, null, null, orderBy);

            while (cursor.moveToNext()) {
                thereAreLogs = true;
                int index;

                index = cursor.getColumnIndex(DatabaseHelper.PROTEIN_CONSUMED);
                double protein = cursor.getDouble(index);

                index = cursor.getColumnIndex(DatabaseHelper.CARBS_CONSUMED);
                double carbs = cursor.getDouble(index);

                index = cursor.getColumnIndex(DatabaseHelper.FAT_CONSUMED);
                double fat = cursor.getDouble(index);

                caloriesWeek = caloriesWeek + (4 * protein + 4 * carbs + 9 * fat);
            }
            cursor.close();

            if (thereAreLogs) {
                caloriesConsumed.add(caloriesWeek);
                weeks.add(week);
            }
        }
        return new WeeklyConsumption(caloriesConsumed, weeks);
    }

    /**
     * Gets the user weight for the last (given) days.
     */
    public WeightLogs getWeightLogs(int numberOfLogs) {
        SQLiteDatabase db = helper.getReadableDatabase();

        // SELECT UserWeight, LogDateTime FROM DailyLogs;
        String[] columns = {DatabaseHelper.USER_WEIGHT, DatabaseHelper.LOG_DATE_TIME};
        String orderBy = DatabaseHelper.LOG_DATE_TIME + " DESC";
        Cursor cursor = db.query(DatabaseHelper.TABLE_DAILY_LOGS, columns,
                null, null, null, null, orderBy, "" + numberOfLogs);

        List<Double> weights = new ArrayList<>();
        List<Long> dateLogs = new ArrayList<>();

        while (cursor.moveToNext()) {
            int index;

            index = cursor.getColumnIndex(DatabaseHelper.USER_WEIGHT);
            weights.add(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.LOG_DATE_TIME);
            dateLogs.add(cursor.getLong(index));
        }
        cursor.close();

        return new WeightLogs(weights, dateLogs);
    }


    // *********************************************************************************************

    /**
     * This inner class takes care of opening the database if it exists,
     * creating it if it does not, and upgrading it as necessary.
     */
    static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "mymacros";

        private static final String TABLE_FOODS = "Foods";
        private static final String FOOD_ID = "_FoodID";
        private static final String NAME = "Name";
        private static final String PROTEIN = "Protein";
        private static final String CARBS = "Carbs";
        private static final String FAT = "Fat";
        private static final String FIBER = "Fiber";
        private static final String PORTION_UNITS = "PortionUnits";
        private static final String PORTION_QUANTITY = "PortionQuantity";

        private static final String TABLE_MEALS = "Meals";
        private static final String MEAL_ID = "_MealID";
        private static final String FOOD_QUANTITY = "foodQuantity";

        private static final String TABLE_MEAL_FOODS = "MealFoods";

        private static final String TABLE_DAILY_LOGS = "DailyLogs";
        private static final String LOG_ID = "_LogID";
        private static final String PROTEIN_TARGET = "ProteinTarget";
        private static final String PROTEIN_CONSUMED = "ProteinConsumed";
        private static final String CARBS_TARGET = "CarbsTarget";
        private static final String CARBS_CONSUMED = "CarbsConsumed";
        private static final String FAT_TARGET = "FatTarget";
        private static final String FAT_CONSUMED = "FatConsumed";
        private static final String USER_WEIGHT = "UserWeight";
        private static final String LOG_DATE_TIME = "LogDateTime";

        private static final String CREATE_FOODS_TABLE =
                "CREATE TABLE " + TABLE_FOODS +
                        " (" + FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NAME + " VARCHAR(50) UNIQUE, " +
                        PORTION_QUANTITY + " REAL, " +
                        PORTION_UNITS + " VARCHAR(4), " +
                        PROTEIN + " REAL, " +
                        CARBS + " REAL, " +
                        FAT + " REAL, " +
                        FIBER + " REAL);";

        private static final String CREATE_MEALS_TABLE =
                "CREATE TABLE " + TABLE_MEALS +
                        " (" + MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NAME + " VARCHAR(50) UNIQUE, " +
                        PROTEIN + " REAL, " +
                        CARBS + " REAL, " +
                        FAT + " REAL, " +
                        FIBER + " REAL);";

        private static final String CREATE_MEAL_FOODS_TABLE =
                "CREATE TABLE " + TABLE_MEAL_FOODS +
                        " (" + MEAL_ID + " INTEGER, " +
                        FOOD_ID + " INTEGER, " +
                        FOOD_QUANTITY + " REAL, " +
                        "PRIMARY KEY (" + MEAL_ID + ", " + FOOD_ID + "));";

/*        private static final String CREATE_MEAL_FOODS_TABLE =
                "CREATE TABLE " + TABLE_MEAL_FOODS +
                        " (" + MEAL_ID + " INTEGER, " +
                        FOOD_ID + " INTEGER, " +
                        FOOD_QUANTITY + " REAL, " +
                        "PRIMARY KEY (" + MEAL_ID + ", " + FOOD_ID + ")," +
                        "FOREIGN KEY(" + MEAL_ID + ") REFERENCES " +
                        TABLE_MEALS + "(" + MEAL_ID +") ON DELETE CASCADE, " +
                        "FOREIGN KEY(" + FOOD_ID + ") REFERENCES " +
                        TABLE_FOODS + "(" + FOOD_ID +") ON DELETE CASCADE);";*/

        private static final String CREATE_DAILY_LOGS_TABLE =
                "CREATE TABLE " + TABLE_DAILY_LOGS +
                        " (" + LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PROTEIN_TARGET + " REAL, " +
                        PROTEIN_CONSUMED + " REAL, " +
                        CARBS_TARGET + " REAL, " +
                        CARBS_CONSUMED + " REAL, " +
                        FAT_TARGET + " REAL, " +
                        FAT_CONSUMED + " REAL, " +
                        USER_WEIGHT + " REAL, " +
                        LOG_DATE_TIME + " INTEGER);";

        private static final int DATABASE_VERSION = 1;

        Context context;

        // Constructor.
        DatabaseHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        /**
         * Called when the database is created for the first time. Creation of tables
         * and initial data inside tables should be put here.
         */
        public void onCreate(SQLiteDatabase db) {

            // Create the Foods table.
            try {
                db.execSQL(CREATE_FOODS_TABLE);
                //Toast.makeText(context, "Database created successfully", Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                e.printStackTrace();
                //Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }

            // Create the Meals table.
            try {
                db.execSQL(CREATE_MEALS_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Create the MealFoods table.
            try {
                db.execSQL(CREATE_MEAL_FOODS_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Create the DailyLogs table.
            try {
                db.execSQL(CREATE_DAILY_LOGS_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        /**
         * Called when the database needs to be upgraded.
         * Used to drop tables, add tables, or do anything else that is needed to
         * upgrade to to new schema version.
         */
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // Might be used to back up the DB in the cloud.
        }
    }
}
