package com.miguelpalacio.mymacros;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DecimalFormat;
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

    // Insert a tuple into the FOODS table.
    public long insertFood(String name, double portionQuantity, String portionUnits,
                           double proteinQuantity, double carbosQuantity,
                           double fatQuantity, double fiberQuantity) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.PORTION_QUANTITY, portionQuantity);
        contentValues.put(DatabaseHelper.PORTION_UNITS, portionUnits);
        contentValues.put(DatabaseHelper.PROTEIN, proteinQuantity);
        contentValues.put(DatabaseHelper.CARBOHYDRATES, carbosQuantity);
        contentValues.put(DatabaseHelper.FAT, fatQuantity);
        contentValues.put(DatabaseHelper.FIBER, fiberQuantity);

        return db.insert(DatabaseHelper.TABLE_FOODS, null, contentValues);
    }

    /**
     * Get information for a given Food ID.
     * @return array of strings with:
     * Name, PortionQuantity, PortionUnits, Protein, Carbohydrates, Fat, Fiber.
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

            index = cursor.getColumnIndex(DatabaseHelper.CARBOHYDRATES);
            food.setCarbohydrates(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FAT);
            food.setFat(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FIBER);
            food.setFiber(cursor.getDouble(index));
        }
        cursor.close();

        return food;
    }

    // Update a tuple from the Foods table given an ID and the updated data.
    public int updateFood(long foodId, String name,double portionQuantity, String portionUnits,
                          double proteinQuantity, double carbosQuantity,
                          double fatQuantity, double fiberQuantity) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.PORTION_QUANTITY, portionQuantity);
        contentValues.put(DatabaseHelper.PORTION_UNITS, portionUnits);
        contentValues.put(DatabaseHelper.PROTEIN, proteinQuantity);
        contentValues.put(DatabaseHelper.CARBOHYDRATES, carbosQuantity);
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

        return db.delete(DatabaseHelper.TABLE_FOODS, whereClause, whereArgs);
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
                DatabaseHelper.PROTEIN, DatabaseHelper.CARBOHYDRATES, DatabaseHelper.FAT,
                DatabaseHelper.PORTION_UNITS};
        String orderBy = DatabaseHelper.NAME;
        Cursor cursor = db.query(DatabaseHelper.TABLE_FOODS, columns,
        null, null, null, null, orderBy + " COLLATE LOCALIZED ASC");

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

            index = cursor.getColumnIndex(DatabaseHelper.CARBOHYDRATES);
            double carbohydrates = cursor.getDouble(index);

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
                    " g, Carbohydrates: " + decimalFormat.format(carbohydrates) +
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


    /**
     * Inserts a new row in the Meals table. Also inserts the information of the foods
     * added to the meal, into the MealFoods table.
     * @return the id given in the database for the new meal.
     */
    public long insertMeal(String name, double proteinQuantity, double carbsQuantity, double fatQuantity,
                           double fiberQuantity, List<String> foodIdList, List<String> foodQuantityList) {
        SQLiteDatabase db = helper.getWritableDatabase();

        long mealId = -1;

        // Perform the insertions into the Meals and MealFoods tables by means of a transaction.
        db.beginTransaction();
        try {
            // Insert Meal's data into the Meals table.
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.NAME, name);
            contentValues.put(DatabaseHelper.PROTEIN, proteinQuantity);
            contentValues.put(DatabaseHelper.CARBOHYDRATES, carbsQuantity);
            contentValues.put(DatabaseHelper.FAT, fatQuantity);
            contentValues.put(DatabaseHelper.FIBER, fiberQuantity);

            mealId = db.insert(DatabaseHelper.TABLE_MEALS, null, contentValues);

            // Insert meal foods' info the MealFoods table.
            for (int i = 0; i < foodIdList.size(); i++) {
                contentValues = new ContentValues();
                contentValues.put(DatabaseHelper.MEAL_ID, mealId);
                contentValues.put(DatabaseHelper.FOOD_ID, Long.parseLong(foodIdList.get(i)));
                contentValues.put(DatabaseHelper.FOOD_QUANTITY, Double.parseDouble(foodQuantityList.get(i)));

                db.insert(DatabaseHelper.TABLE_MEAL_FOODS, null, contentValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return mealId;
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
                DatabaseHelper.PROTEIN, DatabaseHelper.CARBOHYDRATES, DatabaseHelper.FAT};
        String orderBy = DatabaseHelper.NAME;
        Cursor cursor = db.query(DatabaseHelper.TABLE_MEALS, columns,
                null, null, null, null, orderBy + " COLLATE LOCALIZED ASC");

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

            index = cursor.getColumnIndex(DatabaseHelper.CARBOHYDRATES);
            double carbohydrates = cursor.getDouble(index);

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
                    " g, Carbohydrates: " + decimalFormat.format(carbohydrates) +
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
     * Retrieve all the information about the foods belonging to a meal.
     * @param mealId the ID of the meal.
     * @return the foods in that meal, and their respective quantity.
     */
    public List<MealFood> getMealFoods(long mealId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        // Perform a query with an INNER JOIN between Foods and MealFoods.

        Cursor cursor = db.rawQuery(DatabaseHelper.FOODS_JOIN_MEAL_FOODS,
                new String[]{Long.toString(mealId)});

        List<MealFood> mealFoods = new ArrayList<>();

        while (cursor.moveToNext()) {
            int index;
            MealFood mealFood = new MealFood();

            // Get tuple information and add it to mealFood.

            index = cursor.getColumnIndex(DatabaseHelper.FOOD_ID);
            mealFood.setId(cursor.getLong(index));

            index = cursor.getColumnIndex(DatabaseHelper.NAME);
            mealFood.setName(cursor.getString(index));

            index = cursor.getColumnIndex(DatabaseHelper.PORTION_QUANTITY);
            mealFood.setPortionQuantity(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.PORTION_UNITS);
            mealFood.setPortionUnits(cursor.getString(index));

            index = cursor.getColumnIndex(DatabaseHelper.PROTEIN);
            mealFood.setProtein(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.CARBOHYDRATES);
            mealFood.setCarbohydrates(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FAT);
            mealFood.setFat(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FIBER);
            mealFood.setFiber(cursor.getDouble(index));

            index = cursor.getColumnIndex(DatabaseHelper.FOOD_QUANTITY);
            mealFood.setFoodQuantity(cursor.getDouble(index));

            // Add mealFood to the mealFoods list.
            mealFoods.add(mealFood);
        }

        cursor.close();
        return mealFoods;
    }

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
        private static final String CARBOHYDRATES = "Carbohydrates";
        private static final String FAT = "Fat";
        private static final String FIBER = "Fiber";
        private static final String PORTION_UNITS = "PortionUnits";
        private static final String PORTION_QUANTITY = "PortionQuantity";

        private static final String TABLE_MEALS = "Meals";
        private static final String MEAL_ID = "_MealID";
        private static final String FOOD_QUANTITY = "foodQuantity";

        private static final String TABLE_MEAL_FOODS = "MealFoods";

        private static final String CREATE_FOODS_TABLE =
                "CREATE TABLE " + TABLE_FOODS +
                        " (" + FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NAME + " VARCHAR(50) UNIQUE, " +
                        PORTION_QUANTITY + " REAL, " +
                        PORTION_UNITS + " VARCHAR(4), " +
                        PROTEIN + " REAL, " +
                        CARBOHYDRATES + " REAL, " +
                        FAT + " REAL, " +
                        FIBER + " REAL);";

        private static final String CREATE_MEALS_TABLE =
                "CREATE TABLE " + TABLE_MEALS +
                        " (" + MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NAME + " VARCHAR(50) UNIQUE, " +
                        PROTEIN + " REAL, " +
                        CARBOHYDRATES + " REAL, " +
                        FAT + " REAL, " +
                        FIBER + " REAL);";

        private static final String CREATE_MEAL_FOODS_TABLE =
                "CREATE TABLE " + TABLE_MEAL_FOODS +
                        " (" + MEAL_ID + " INTEGER, " +
                        FOOD_ID + " INTEGER, " +
                        FOOD_QUANTITY + " REAL, " +
                        "PRIMARY KEY (" + MEAL_ID + ", " + FOOD_ID + "));";

        private static final String FOODS_JOIN_MEAL_FOODS =
                "SELECT " + FOOD_ID + ", " + NAME + ", " + PORTION_QUANTITY + ", " +
                        PORTION_UNITS + ", " + PROTEIN + ", " + CARBOHYDRATES + ", " +
                        FAT + ", " + FIBER + ", " + FOOD_QUANTITY +
                        " FROM " + TABLE_FOODS + " INNER JOIN " + TABLE_MEAL_FOODS + " ON " +
                        TABLE_FOODS + "." + FOOD_ID + " = " + TABLE_MEAL_FOODS + "." + FOOD_ID +
                        " WHERE " + MEAL_ID + " = ?";

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
