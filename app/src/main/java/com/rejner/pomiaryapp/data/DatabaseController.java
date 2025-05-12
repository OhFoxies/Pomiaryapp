package com.rejner.pomiaryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseController extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Pomiary.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TablesController.Pomiary.TABLE_NAME + " (" +
                    TablesController.Pomiary._ID + " INTEGER PRIMARY KEY," +
                    TablesController.Pomiary.COLUMN_NAME_NAME + " VARCHAR(255), " +
                    TablesController.Pomiary.COLUMN_NAME_DATE + " DATE DEFAULT CURRENT_DATE); " +
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
                    "REFERENCES " + TablesController.Bloki.TABLE_NAME + "(" + TablesController.Bloki._ID + "));"
                    + "CREATE TABLE " + TablesController.Pokoj.TABLE_NAME +
                    " (" + TablesController.Pokoj._ID + " INTEGER PRIMARY KEY, " +
                    TablesController.Pokoj.COLUMN_NAME_TYPE + "VARCHAR(255), " +
                    TablesController.Pokoj.COLUMN_NAME_FLAT_ID + "INTEGER, " +
                    "FOREIGN KEY (" + TablesController.Pokoj.COLUMN_NAME_FLAT_ID + ") " +
                    "REFERENCES " + TablesController.Pokoj.TABLE_NAME + "(" + TablesController.Pokoj._ID + "));"
                    + "CREATE TABLE " + TablesController.Gniazdko.TABLE_NAME +
                    " (" + TablesController.Gniazdko._ID + " INTEGER PRIMARY KEY, " +
                    TablesController.Gniazdko.COLUMN_NAME_COMMENT + "TEXT, " +
                    TablesController.Gniazdko.COLUMN_NAME_MEASUREMENT + "VARCHAR(255), " +
                    TablesController.Gniazdko.COLUMN_NAME_ROOM_ID + "INTEGER, " +
                    "FOREIGN KEY (" + TablesController.Gniazdko.COLUMN_NAME_ROOM_ID + ") " +
                    "REFERENCES " + TablesController.Pokoj.TABLE_NAME + "(" + TablesController.Pokoj._ID + "));"
                    + "CREATE TABLE " + TablesController.Zdjecia.TABLE_NAME +
                    " (" + TablesController.Zdjecia._ID + " INTEGER PRIMARY KEY, " +
                    TablesController.Zdjecia.COLUMN_NAME_IMAGE + "BLOB, " +
                    TablesController.Zdjecia.COLUMN_NAME_FLAT_ID + "INTEGER" +
                    "FOREIGN KEY (" + TablesController.Zdjecia.COLUMN_NAME_FLAT_ID + ") " +
                    "REFERENCES " + TablesController.Mieszkanie.TABLE_NAME + "(" + TablesController.Mieszkanie._ID + "));";








    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TablesController.Pomiary.TABLE_NAME + "; " +
                    "DROP TABLE IF EXISTS " + TablesController.Bloki.TABLE_NAME + "; " +
                    "DROP TABLE IF EXISTS " + TablesController.Mieszkanie.TABLE_NAME + "; " +
                    "DROP TABLE IF EXISTS " + TablesController.Pokoj.TABLE_NAME + "; " +
                    "DROP TABLE IF EXISTS " + TablesController.Zdjecia.TABLE_NAME + "; " +
                    "DROP TABLE IF EXISTS " + TablesController.Gniazdko.TABLE_NAME;


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

    // query do przerobienia
    public Bitmap getImageById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Bitmap bitmap = null;

        Cursor cursor = db.rawQuery("SELECT " + TablesController.Zdjecia.COLUMN_NAME_IMAGE +  " from " + TablesController.Zdjecia.TABLE_NAME +  " WHERE id = ?", new String[]{String.valueOf(id)});

        if(cursor != null && cursor.moveToFirst()){
            byte[] imageBytes = cursor.getBlob(0);
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            cursor.close();

        }
        db.close();
        return bitmap;
    }

    public boolean doesMeasurementExist(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                TablesController.Pomiary.COLUMN_NAME_NAME,
        };

        String selection = TablesController.Pomiary.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = db.query(
                TablesController.Pomiary.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<Long> ids = new ArrayList<>();
        while(cursor.moveToNext()) {
            Long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(TablesController.Pomiary._ID));
            ids.add(id);
        }
        cursor.close();
        return !ids.isEmpty();
    }
}
