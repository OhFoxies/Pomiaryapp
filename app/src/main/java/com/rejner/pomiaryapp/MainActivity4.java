package com.rejner.pomiaryapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rejner.pomiaryapp.data.DatabaseController;
import com.rejner.pomiaryapp.data.TablesController;

import java.util.List;

public class MainActivity4 extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main4);
        Intent intent = getIntent();
        int id = intent.getIntExtra("measurementID", 0);

        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        TablesController.Pomiar measurement = dbHelper.getMeasurementByID(id);

        TextView titleView = findViewById(R.id.title);
        titleView.setText("Pomiar: " + measurement.name);


    }

    private void reloadHomes() {
        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TablesController.Home> homes = dbHelper.getAllHomes();

        LinearLayout container = findViewById(R.id.measurements);
        LayoutInflater inflater = LayoutInflater.from(this);
        container.removeAllViews();

        for (TablesController.Pomiar p : measurements) {
            View itemView = inflater.inflate(R.layout.item_measurement, container, false);

            TextView nameText = itemView.findViewById(R.id.measurementName);
            TextView dateText = itemView.findViewById(R.id.measurementDate);
            Button button = itemView.findViewById(R.id.showButton);
            Button delete = itemView.findViewById(R.id.deleteButton);

            nameText.setText(p.name);
            dateText.setText(p.date);
            Log.d("Measurement", "Name: " + p.name + ", Date: " + p.date);

            button.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity4.class);
                intent.putExtra("measurementID", p.id);

                startActivity(intent);
            });




            delete.setOnClickListener(view -> {
                dbHelper.deleteById(p.id,this, TablesController.Pomiary.TABLE_NAME);
                reloadMeasurements();
            });

            container.addView(itemView);
        }
    }
}