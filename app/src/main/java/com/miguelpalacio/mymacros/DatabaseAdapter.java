package com.miguelpalacio.mymacros;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * This class handles everything related with the application database.
 */
public class DatabaseAdapter {

    DatabaseHelper helper;

    public DatabaseAdapter(Context context) {
        helper = new DatabaseHelper(context);
    }

    // Insert a tuple into the FOODS table.
    public long insertFood(String name, int protein, int carbohydrates, int fat,
                           int fiber, int portionQuantity, int portionUnits) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.PROTEIN, protein);
        contentValues.put(DatabaseHelper.CARBOHYDRATES, carbohydrates);
        contentValues.put(DatabaseHelper.FAT, fat);
        contentValues.put(DatabaseHelper.FIBER, fiber);
        contentValues.put(DatabaseHelper.PORTION_QUANTITY, portionQuantity);
        contentValues.put(DatabaseHelper.PORTION_UNITS, portionUnits);

        return db.insert(DatabaseHelper.TABLE_FOODS, null, contentValues);
    }

    /**
     * This inner class takes care of opening the database if it exists,
     * creating it if it does not, and upgrading it as necessary.
     */
    static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "mymacros";

        private static final String TABLE_FOODS = "Foods";
        private static final String FOOD_UID = "_FoodID";
        private static final String NAME = "Name";
        private static final String PROTEIN = "Protein";
        private static final String CARBOHYDRATES = "Carbohydrates";
        private static final String FAT = "Fat";
        private static final String FIBER = "Fiber";
        private static final String PORTION_UNITS = "PortionUnits";
        private static final String PORTION_QUANTITY = "PortionQuantity";

        private static final String CREATE_FOODS_TABLE = "CREATE TABLE " + TABLE_FOODS +
                " (" + FOOD_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
