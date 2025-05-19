package com.rejner.pomiaryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
        this.reloadMeasurements();
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
                    reloadMeasurements();
                }
            }
        });

    }
    private void reloadMeasurements() {
        List<TablesController.Pomiar> measurements = dbHelper.getAllMeasurements();
        LinearLayout container = findViewById(R.id.measurements);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (TablesController.Pomiar p : measurements) {
            View itemView = inflater.inflate(R.layout.item_measurement, container, false);

            TextView nameText = itemView.findViewById(R.id.measurementName);
            TextView dateText = itemView.findViewById(R.id.measurementDate);
            Button button = itemView.findViewById(R.id.showButton);

            nameText.setText(p.name);
            dateText.setText(p.date);

            button.setOnClickListener(v -> {
                Toast.makeText(this, "Kliknięto: " + p.name, Toast.LENGTH_SHORT).show();
            });

            container.addView(itemView);
        }
    }
}