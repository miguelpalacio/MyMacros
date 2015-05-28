package com.miguelpalacio.mymacros;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DecimalFormat;
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
    public long insertFood(String name, double portionQuantity, int portionUnits,
                           double proteinQuantity, double carbosQuantity,
                           double fatQuantity, double fiberQuantity) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Decimal format to prepare the data prior to insertion into database.
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        proteinQuantity = Double.parseDouble(decimalFormat.format(proteinQuantity));
        carbosQuantity = Double.parseDouble(decimalFormat.format(carbosQuantity));
        fatQuantity = Double.parseDouble(decimalFormat.format(fatQuantity));
        fiberQuantity = Double.parseDouble(decimalFormat.format(fiberQuantity));
        portionQuantity = Double.parseDouble(decimalFormat.format(portionQuantity));

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
    public String[] getFoodInfo(long foodId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String column = "*";
        String[] columns = {column};
        String selection = DatabaseHelper.FOOD_ID + " = ?";
        String[] selectionArgs = {Double.toString(foodId)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_FOODS, columns,
                selection, selectionArgs, null, null, null);

        String[] foodInfo = new String[7];
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        while (cursor.moveToNext()) {
            int index;

            index = cursor.getColumnIndex(DatabaseHelper.NAME);
            foodInfo[0] = cursor.getString(index);
            index = cursor.getColumnIndex(DatabaseHelper.PORTION_QUANTITY);
            foodInfo[1] = decimalFormat.format(cursor.getDouble(index));
            index = cursor.getColumnIndex(DatabaseHelper.PORTION_UNITS);
            foodInfo[2] = Integer.toString(cursor.getInt(index));
            index = cursor.getColumnIndex(DatabaseHelper.PROTEIN);
            foodInfo[3] = decimalFormat.format(cursor.getDouble(index));
            index = cursor.getColumnIndex(DatabaseHelper.CARBOHYDRATES);
            foodInfo[4] = decimalFormat.format(cursor.getDouble(index));
            index = cursor.getColumnIndex(DatabaseHelper.FAT);
            foodInfo[5] = decimalFormat.format(cursor.getDouble(index));
            index = cursor.getColumnIndex(DatabaseHelper.FIBER);
            foodInfo[6] = decimalFormat.format(cursor.getDouble(index));
        }
        cursor.close();

        return foodInfo;
    }

    // Update a tuple from the Foods table given an ID and the updated data.
    public int updateFood(long foodId, String name,double portionQuantity, int portionUnits,
                          double proteinQuantity, double carbosQuantity,
                          double fatQuantity, double fiberQuantity) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Decimal format to prepare the data prior to insertion into database.
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        proteinQuantity = Double.parseDouble(decimalFormat.format(proteinQuantity));
        carbosQuantity = Double.parseDouble(decimalFormat.format(carbosQuantity));
        fatQuantity = Double.parseDouble(decimalFormat.format(fatQuantity));
        fiberQuantity = Double.parseDouble(decimalFormat.format(fiberQuantity));
        portionQuantity = Double.parseDouble(decimalFormat.format(portionQuantity));

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

    public int deleteFood(long foodId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = DatabaseHelper.FOOD_ID + " = ?";
        String[] whereArgs = {Long.toString(foodId)};

        return db.delete(DatabaseHelper.TABLE_FOODS, whereClause, whereArgs);
    }

    /**
     * Retrieve the name and a summary for each item in the Foods table.
     * @return four arrays:
     * info[0]: contains the list of food ids.
     * info[1]: contains the list of food names and subheaders for the food list.
     * info[2]: contains the summaries
     * info[3]: it's "boolean" array expressing which position corresponds to a subheader.
     */
    public String[][] getFoods() {
        SQLiteDatabase db = helper.getReadableDatabase();

        // SELECT Name, Protein, Carbohydrates, Fat FROM Foods;
        String[] columns = {DatabaseHelper.FOOD_ID, DatabaseHelper.NAME,
                DatabaseHelper.PROTEIN, DatabaseHelper.CARBOHYDRATES, DatabaseHelper.FAT};
        String orderBy = DatabaseHelper.NAME;
        Cursor cursor = db.query(DatabaseHelper.TABLE_FOODS, columns,
/*                null, null, null, null, orderBy);*/
        null, null, null, null, orderBy + " COLLATE LOCALIZED ASC");

        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> summaries = new ArrayList<>();
        ArrayList<String> isSubheader = new ArrayList<>();

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
            String foodSummary = "Protein: " + decimalFormat.format(protein) +
                    " g, Carbohydrates: " + decimalFormat.format(carbohydrates) +
                    " g, Fat: " + decimalFormat.format(fat) + " g";
            summaries.add(foodSummary);
        }
        cursor.close();

        // Store the resulting ArrayLists in a single data structure, and return it.
        String[][] info = new String[4][ids.size()];
        info[0] = ids.toArray(new String[ids.size()]);
        info[1] = names.toArray(new String[ids.size()]);
        info[2] = summaries.toArray(new String[ids.size()]);
        info[3] = isSubheader.toArray(new String[ids.size()]);

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

        private static final String CREATE_FOODS_TABLE =
                "CREATE TABLE " + TABLE_FOODS +
                        " (" + FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NAME + " VARCHAR(50) UNIQUE, " +
                        PORTION_QUANTITY + " NUMERIC(5,1), " +
                        PORTION_UNITS + " INTEGER, " +
                        PROTEIN + " NUMERIC(5,1), " +
                        CARBOHYDRATES + " NUMERIC(5,1), " +
                        FAT + " NUMERIC(5,1), " +
                        FIBER + " NUMERIC(5,1));";

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

            // Create the FOOD table.
            try {
                db.execSQL(CREATE_FOODS_TABLE);
                //Toast.makeText(context, "Database created successfully", Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                e.printStackTrace();
                //Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
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
