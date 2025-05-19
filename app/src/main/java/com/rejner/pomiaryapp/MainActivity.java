package com.rejner.pomiaryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rejner.pomiaryapp.data.DatabaseController;
import com.rejner.pomiaryapp.data.TablesController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        DatabaseController dbHelper = new DatabaseController(this);
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TablesController.Pomiary.COLUMN_NAME_NAME, "Pomiary osiedle chuja");

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TablesController.Pomiary.TABLE_NAME, null, values);

        db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                TablesController.Pomiary.COLUMN_NAME_NAME,
        };

// Filter results WHERE "title" = 'My Title'
        String selection = TablesController.Pomiary.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { "My Title" };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                TablesController.Pomiary.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = db.query(
                TablesController.Pomiary.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,
                null,        // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<String> itemNames = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemName = cursor.getString(
                    cursor.getColumnIndexOrThrow(TablesController.Pomiary.COLUMN_NAME_NAME));
            itemNames.add(itemName);
        }
        cursor.close();

//        TextView text = findViewById(R.id.test);
//        text.setText(itemNames.get(0));
        Button button = findViewById(R.id.createMeasurement);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.MeasurementName);
                if (editText.getText().toString().isEmpty()) {
                    TextView errorView = findViewById(R.id.CreationFeedback);
                    errorView.setText("Nie podano nazwy pomiaru");
                } if ()
            }
        });
    }
}