package com.miguelpalacio.mymacros;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class takes care of opening the database if it exists,
 * creating it if it does not, and upgrading it as necessary.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mymacros";

    private static final String TABLE_FOOD = "FOOD";
    private static final String UID = "_id";
    private static final String NAME = "Name";
    private static final String PROTEIN = "Protein";
    private static final String CARBOHYDRATES = "Carbohydrates";
    private static final String FAT = "Fat";
    private static final String FIBER = "Fiber";
    private static final String PORTION_TYPE = "Portion_Type";
    private static final String QUANTITY = "Quantity";

    private static final int DATABASE_VERSION = 1;

    // Constructor.
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. Creation of tables
     * and initial data inside tables should be put here.
     */
    public void onCreate(SQLiteDatabase db) {

        // Create the FOOD table.
        try {
            db.execSQL("CREATE TABLE " + TABLE_FOOD +
                    " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " VARCHAR(50) UNIQUE, " +
                    PROTEIN + " NUMERIC(5,1), " +
                    CARBOHYDRATES + " NUMERIC(5,1), " +
                    FAT + " NUMERIC(5,1), " +
                    FIBER + " NUMERIC(5,1), " +
                    PORTION_TYPE + " BIT(1), " +
                    QUANTITY + " INTEGER));");
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
