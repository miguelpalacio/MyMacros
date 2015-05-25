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
    public long insertFood(String name, double proteinQuantity, double carbosQuantity,
                           double fatQuantity, double fiberQuantity, double portionQuantity,
                           int portionUnits) {

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
        contentValues.put(DatabaseHelper.PROTEIN, proteinQuantity);
        contentValues.put(DatabaseHelper.CARBOHYDRATES, carbosQuantity);
        contentValues.put(DatabaseHelper.FAT, fatQuantity);
        contentValues.put(DatabaseHelper.FIBER, fiberQuantity);
        contentValues.put(DatabaseHelper.PORTION_QUANTITY, portionQuantity);
        contentValues.put(DatabaseHelper.PORTION_UNITS, portionUnits);

        return db.insert(DatabaseHelper.TABLE_FOODS, null, contentValues);
    }

    // Select a tuple from the Foods table given an ID.
    public String[] getFoodInfo(int foodId) {

        String [] a = {"2", "3"};
        return a;
    }

    // Update a tuple from the Foods table given an ID and the updated data.
    public int updateFood(long foodId, String name, double proteinQuantity, double carbosQuantity,
                           double fatQuantity, double fiberQuantity, double portionQuantity,
                           int portionUnits) {
        return 0;
    }

    /**
     * Retrieve the name and a summary for each item in the Foods table.
     * @return three arrays: the first array contains the list of titles and
     * subheaders for the food list, the second the summaries, and the third a
     * "boolean" array expressing which position correspond to a subheader.
     */
    public String[][] getFoods() {
        SQLiteDatabase db = helper.getReadableDatabase();

        // SELECT Name, Protein, Carbohydrates, Fat FROM Foods;
        String[] columns = {DatabaseHelper.NAME, DatabaseHelper.PROTEIN,
                DatabaseHelper.CARBOHYDRATES, DatabaseHelper.FAT};
        String orderBy = DatabaseHelper.NAME;
        Cursor cursor = db.query(DatabaseHelper.TABLE_FOODS, columns,
                null, null, null, null, orderBy);

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> summaries = new ArrayList<>();
        ArrayList<String> isSubheader = new ArrayList<>();

        Character lastSubheader = '\0';
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        while (cursor.moveToNext()) {
            int index;

            // Get Food information.
            index = cursor.getColumnIndex(DatabaseHelper.NAME);
            String name = cursor.getString(index);
            index = cursor.getColumnIndex(DatabaseHelper.PROTEIN);
            double protein = cursor.getDouble(index);
            index = cursor.getColumnIndex(DatabaseHelper.CARBOHYDRATES);
            double carbohydrates = cursor.getDouble(index);
            index = cursor.getColumnIndex(DatabaseHelper.FAT);
            double fat = cursor.getDouble(index);

            // Define if a lastSubheader should be placed.
            if (lastSubheader != name.charAt(0)) {
                // Check for numeric characters and special symbols.
                if (Character.isLetter(name.charAt(0))) {
                    lastSubheader = name.charAt(0);
                    isSubheader.add("1");
                    names.add(lastSubheader.toString().toUpperCase());
                    summaries.add("");
                }
                // If special character, set subheader as '#' if it hasn't been set.
                else if (lastSubheader != '#') {
                    lastSubheader = '#';
                    names.add(lastSubheader.toString());
                    isSubheader.add("1");
                    summaries.add("");
                }
            }
            names.add(name);
            isSubheader.add("0");

            // Parse data and place it in summaries.
            String foodSummary = "Protein: " + decimalFormat.format(protein) +
                    " gr, Carbohydrates: " + decimalFormat.format(carbohydrates) +
                    " gr, Fat: " + decimalFormat.format(fat) + " gr";
            summaries.add(foodSummary);
        }
        cursor.close();

        // Store the resulting ArrayLists in a single data structure, and return it.
        String[][] info = new String[3][names.size()];
        info[0] = names.toArray(new String[names.size()]);
        info[1] = summaries.toArray(new String[names.size()]);
        info[2] = isSubheader.toArray(new String[names.size()]);

        return info;
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
                PROTEIN + " NUMERIC(5,1), " +
                CARBOHYDRATES + " NUMERIC(5,1), " +
                FAT + " NUMERIC(5,1), " +
                FIBER + " NUMERIC(5,1), " +
                PORTION_UNITS + " INTEGER, " +
                PORTION_QUANTITY + " NUMERIC(5,1));";

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
