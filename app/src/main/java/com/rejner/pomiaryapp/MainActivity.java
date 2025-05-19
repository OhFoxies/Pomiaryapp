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

    DatabaseController dbHelper = new DatabaseController(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.createMeasurement);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText measurementName = findViewById(R.id.MeasurementName);
                if (measurementName.getText().toString().isEmpty()) {
                    TextView errorView = findViewById(R.id.CreationFeedback);
                    errorView.setText("Nie podano nazwy pomiaru");
                } if (dbHelper.doesMeasurementExist(measurementName.toString())) {
                    TextView errorView = findViewById(R.id.CreationFeedback);
                    errorView.setText("Pomiar o tej nazwie już istnieje");
                } else {
                    ContentValues values = new ContentValues();
                    values.put(TablesController.Pomiary.COLUMN_NAME_NAME, measurementName.toString());

                    db.insert(TablesController.Pomiary.TABLE_NAME, null, values);
                }
            }
        });
    }
    private void reloadMeasurements() {
        List<TablesController.Pomiar> measurements = dbHelper.getAllMeasurements();

    }
}