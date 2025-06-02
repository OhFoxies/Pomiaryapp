package com.rejner.pomiaryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.rejner.pomiaryapp.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class DatabaseController extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "Pomiary.db";
    private static final String[] SQL_CREATE_ENTRIES = {
                    "CREATE TABLE " + TablesController.Pomiary.TABLE_NAME + " (" +
                    TablesController.Pomiary._ID + " INTEGER PRIMARY KEY, " +
                    TablesController.Pomiary.COLUMN_NAME_NAME + " VARCHAR(255), " +
                    TablesController.Pomiary.COLUMN_NAME_DATE + " DATE DEFAULT CURRENT_DATE); ",

                    "CREATE TABLE " + TablesController.Bloki.TABLE_NAME + " (" +
                    TablesController.Bloki._ID + " INTEGER PRIMARY KEY, " +
                    TablesController.Bloki.COLUMN_NAME_ID_MEASUREMENT + " INTEGER, " +
                    TablesController.Bloki.COLUMN_NAME_CITY + " VARCHAR(255), " +
                    TablesController.Bloki.COLUMN_NAME_STREET + " VARCHAR(255), " +
                    TablesController.Bloki.COLUMN_NAME_NUMBER + " VARCHAR(255), " +
                    "FOREIGN KEY (" + TablesController.Bloki.COLUMN_NAME_ID_MEASUREMENT + ") " +
                    "REFERENCES " + TablesController.Pomiary.TABLE_NAME + "(" + TablesController.Pomiary._ID + ")); ",

                    "CREATE TABLE " + TablesController.Mieszkanie.TABLE_NAME + " (" +
                    TablesController.Mieszkanie._ID + " INTEGER PRIMARY KEY, " + //
                    TablesController.Mieszkanie.COLUMN_NAME_NUMBER + " VARCHAR(255), " +
                    TablesController.Mieszkanie.COLUMN_NAME_HOME_ID + " INTEGER, " +
                    "FOREIGN KEY (" + TablesController.Mieszkanie.COLUMN_NAME_HOME_ID + ") " +
                    "REFERENCES " + TablesController.Bloki.TABLE_NAME + "(" + TablesController.Bloki._ID + ")); ",

                    "CREATE TABLE " + TablesController.Pokoj.TABLE_NAME + " (" +
                    TablesController.Pokoj._ID + " INTEGER PRIMARY KEY, " +
                    TablesController.Pokoj.COLUMN_NAME_TYPE + " VARCHAR(255), " +
                    TablesController.Pokoj.COLUMN_NAME_FLAT_ID + " INTEGER, " +
                    "FOREIGN KEY (" + TablesController.Pokoj.COLUMN_NAME_FLAT_ID + ") " +
                    "REFERENCES " + TablesController.Pokoj.TABLE_NAME + "(" + TablesController.Pokoj._ID + ")); ",

                    "CREATE TABLE " + TablesController.Gniazdko.TABLE_NAME + " (" +
                    TablesController.Gniazdko._ID + " INTEGER PRIMARY KEY, " +
                    TablesController.Gniazdko.COLUMN_NAME_COMMENT + " TEXT, " +
                    TablesController.Gniazdko.COLUMN_NAME_MEASUREMENT + " VARCHAR(255), " +
                    TablesController.Gniazdko.COLUMN_NAME_ROOM_ID + " INTEGER, " +
                    "FOREIGN KEY (" + TablesController.Gniazdko.COLUMN_NAME_ROOM_ID + ") " +
                    "REFERENCES " + TablesController.Pokoj.TABLE_NAME + "(" + TablesController.Pokoj._ID + ")); ",

                    "CREATE TABLE " + TablesController.Zdjecia.TABLE_NAME + " (" +
                    TablesController.Zdjecia._ID + " INTEGER PRIMARY KEY, " +
                    TablesController.Zdjecia.COLUMN_NAME_IMAGE + " TEXT, " +
                    TablesController.Zdjecia.COLUMN_NAME_FLAT_ID + " INTEGER, " +
                    "FOREIGN KEY (" + TablesController.Zdjecia.COLUMN_NAME_FLAT_ID + ") " +
                    "REFERENCES " + TablesController.Mieszkanie.TABLE_NAME + "(" + TablesController.Mieszkanie._ID + "));" };









    private static final String[] SQL_DELETE_ENTRIES =
            { "DROP TABLE IF EXISTS " + TablesController.Pomiary.TABLE_NAME + "; ",
                    "DROP TABLE IF EXISTS " + TablesController.Bloki.TABLE_NAME + "; ",
                    "DROP TABLE IF EXISTS " + TablesController.Mieszkanie.TABLE_NAME + "; ",
                    "DROP TABLE IF EXISTS " + TablesController.Pokoj.TABLE_NAME + "; ",
                    "DROP TABLE IF EXISTS " + TablesController.Zdjecia.TABLE_NAME + "; ",
                    "DROP TABLE IF EXISTS " + TablesController.Gniazdko.TABLE_NAME};


    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String createTable : SQL_CREATE_ENTRIES) {
            db.execSQL(createTable);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        for (String deleteTable : SQL_DELETE_ENTRIES) {
            db.execSQL(deleteTable);
        }
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




    public List<TablesController.Pomiar> getAllMeasurements() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                TablesController.Pomiary.COLUMN_NAME_NAME,
                TablesController.Pomiary.COLUMN_NAME_DATE
        };


        Cursor cursor = db.query(
                TablesController.Pomiary.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                TablesController.Pomiary.COLUMN_NAME_DATE
        );

        List<TablesController.Pomiar> measurements = new ArrayList<>();
        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TablesController.Pomiary._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(TablesController.Pomiary.COLUMN_NAME_NAME));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(TablesController.Pomiary.COLUMN_NAME_DATE));

            measurements.add(new TablesController.Pomiar(id, name, date));
        }
        cursor.close();

        return measurements;
    }
    public void deleteById(long id, Context context, String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        Toast.makeText(context, String.valueOf(id), Toast.LENGTH_SHORT).show();
        db.delete(tableName, TablesController.Pomiary._ID + "=?", new String[]{String.valueOf(id)});

        db.close();
    }

    public TablesController.Pomiar getMeasurementByID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                TablesController.Pomiary.COLUMN_NAME_NAME,
                TablesController.Pomiary.COLUMN_NAME_DATE
        };

        String selection = TablesController.Pomiary._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.query(
                TablesController.Pomiary.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        TablesController.Pomiar result = null;

        if (cursor != null && cursor.moveToFirst()) {
            long id_ = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(TablesController.Pomiary.COLUMN_NAME_NAME));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(TablesController.Pomiary.COLUMN_NAME_DATE));
            result = new TablesController.Pomiar(id_, name, date);
        } else {
            Log.e("DB_ERROR", "No measurement found for ID: " + id);
        }

        if (cursor != null) cursor.close();

        return result;
    }

    public List<TablesController.Home> getAllHomes(long measurementId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                TablesController.Bloki.COLUMN_NAME_ID_MEASUREMENT,
                TablesController.Bloki.COLUMN_NAME_STREET,
                TablesController.Bloki.COLUMN_NAME_NUMBER,
                TablesController.Bloki.COLUMN_NAME_CITY,
        };

        String selection = TablesController.Bloki.COLUMN_NAME_ID_MEASUREMENT + " = ?";

        String[] selectionArgs = {Long.toString(measurementId)};
        Log.e("tak",selectionArgs[0]);

        Cursor cursor = db.query(
                TablesController.Bloki.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<TablesController.Home> homes = new ArrayList<>();
        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TablesController.Bloki._ID));
            long measurement_id = cursor.getLong(cursor.getColumnIndexOrThrow(TablesController.Bloki.COLUMN_NAME_ID_MEASUREMENT));
            String street = cursor.getString(cursor.getColumnIndexOrThrow(TablesController.Bloki.COLUMN_NAME_STREET));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(TablesController.Bloki.COLUMN_NAME_NUMBER));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(TablesController.Bloki.COLUMN_NAME_CITY));

            homes.add(new TablesController.Home(id, measurement_id, street, city, number));
        }
        cursor.close();

        return homes;
    }

    public boolean doesFlatExist(String name, long foreign_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                TablesController.Mieszkanie.COLUMN_NAME_NUMBER,
        };

        String selection = TablesController.Mieszkanie.COLUMN_NAME_NUMBER + " = ? AND " + TablesController.Mieszkanie.COLUMN_NAME_HOME_ID + " =?";
        String[] selectionArgs = { name, Long.toString(foreign_id) };

        Cursor cursor = db.query(
                TablesController.Mieszkanie.TABLE_NAME,
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
                    cursor.getColumnIndexOrThrow(TablesController.Mieszkanie._ID));
            ids.add(id);
        }
        cursor.close();
        return !ids.isEmpty();
    }


    public List<TablesController.Flat> getAllFlats(long foreign_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                TablesController.Mieszkanie.COLUMN_NAME_NUMBER,
                TablesController.Mieszkanie.COLUMN_NAME_HOME_ID
        };

        String selection = TablesController.Mieszkanie.COLUMN_NAME_HOME_ID + " = ?";

        String[] selectionArgs = {Long.toString(foreign_id)};

        Cursor cursor = db.query(
                TablesController.Mieszkanie.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                TablesController.Mieszkanie.COLUMN_NAME_NUMBER
        );

        List<TablesController.Flat> flats = new ArrayList<>();
        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TablesController.Mieszkanie._ID));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(TablesController.Mieszkanie.COLUMN_NAME_NUMBER));

            flats.add(new TablesController.Flat(id, number, foreign_id));
        }
        cursor.close();

        return flats;
    }
}
