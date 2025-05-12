package com.rejner.pomiaryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseController extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Pomiary.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TablesController.Pomiary.TABLE_NAME + " (" +
                    TablesController.Pomiary._ID + " INTEGER PRIMARY KEY," +
                    TablesController.Pomiary.COLUMN_NAME_NAME + " VARCHAR(255)); " +
                    "CREATE TABLE " + TablesController.Bloki.TABLE_NAME +
                    " (" + TablesController.Bloki._ID + " INTEGER PRIMARY KEY, "
                    + TablesController.Bloki.COLUMN_NAME_ID_MEASUREMENT + " INTEGER, "
                    + TablesController.Bloki.COLUMN_NAME_CITY + "VARCHAR(255), "
                    + TablesController.Bloki.COLUMN_NAME_STREET + "VARCHAR(255), "
                    + TablesController.Bloki.COLUMN_NAME_NUMBER + "VARCHAR(255), "
                    + "FOREIGN KEY (" + TablesController.Bloki.COLUMN_NAME_ID_MEASUREMENT + ")"
                    + "REFERENCES " + TablesController.Pomiary.TABLE_NAME + "(" + TablesController.Pomiary._ID +"));"
                    + "CREATE TABLE " + TablesController.Mieszkanie.TABLE_NAME + " ("
                    + TablesController.Mieszkanie.COLUMN_NAME_NUMBER + "VARCHAR(255), " +
                    TablesController.Mieszkanie.COLUMN_NAME_DATE + "DATE DEFAULT CURRENT_DATE, " +
                    TablesController.Mieszkanie.COLUMN_NAME_HOME_ID + "INTEGER, " +
                    "FOREIGN KEY (" + TablesController.Mieszkanie.COLUMN_NAME_HOME_ID + ") " +
                    "REFERENCES " + TablesController.Bloki.TABLE_NAME + "(" + TablesController.Bloki._ID + "))";




    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TablesController.Pomiary.TABLE_NAME;


    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
