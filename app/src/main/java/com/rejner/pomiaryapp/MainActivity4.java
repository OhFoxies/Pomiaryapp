package com.rejner.pomiaryapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        int measurementId = intent.getIntExtra("measurementId", 0);
        String measurementName = intent.getStringExtra("measurementName");



        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        TablesController.Pomiar measurement = dbHelper.getMeasurementByID(id);

        TextView titleView = findViewById(R.id.title);
        titleView.setText("Pomiar: " + measurementName);



        this.reloadHomes();

        Button button = findViewById(R.id.createHome);
        Log.e("siur", "1");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText street = findViewById(R.id.City);
                EditText city = findViewById(R.id.Street);
                EditText number = findViewById(R.id.Number);
                Log.e("siur", "2");

                if (street.getText().toString().isEmpty() || city.getText().toString().isEmpty() || number.getText().toString().isEmpty()) {
                    TextView errorView = findViewById(R.id.CreationFeedback);
                    errorView.setText("Nie podano wszystkich danych");
                    Log.e("siur", "4");

                } else {
                    Log.e("siur", "3");

                    ContentValues values = new ContentValues();
                    street.clearFocus();
                    city.clearFocus();
                    number.clearFocus();

                    TextView errorView = findViewById(R.id.CreationFeedback);
                    errorView.setTextColor(Color.rgb(72, 245, 66));
                    errorView.setText("Dom został dodany");

                    values.put(TablesController.Bloki.COLUMN_NAME_CITY, city.getText().toString());
                    values.put(TablesController.Bloki.COLUMN_NAME_NUMBER, number.getText().toString());
                    values.put(TablesController.Bloki.COLUMN_NAME_ID_MEASUREMENT, measurementId);
                    values.put(TablesController.Bloki.COLUMN_NAME_STREET, street.getText().toString());
                    street.setText("");
                    city.setText("");
                    number.setText("");

                    db.insert(TablesController.Bloki.TABLE_NAME, null, values);
                    reloadHomes();
                }
                Log.e("siur", "5");

            }

        });




    }

    private void reloadHomes() {
        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TablesController.Home> homes = dbHelper.getAllHomes();

        LinearLayout container = findViewById(R.id.homes);
        LayoutInflater inflater = LayoutInflater.from(this);
        container.removeAllViews();

        for (TablesController.Home home : homes) {
            View itemView = inflater.inflate(R.layout.item_home, container, false);

            TextView city = itemView.findViewById(R.id.city);
            TextView street = itemView.findViewById(R.id.street);
            TextView number = itemView.findViewById(R.id.number);

            Button delete = itemView.findViewById(R.id.deleteButton);
            Button showHome = itemView.findViewById(R.id.showHome);

            showHome.setOnClickListener(view -> {
                Intent intent = new Intent(this, MainActivity5.class);
                intent.putExtra("homeID", home.id);
                intent.putExtra("homeName", home.street + "/" + home.number);

                startActivity(intent);
            });


            city.setText(home.city);
            number.setText(home.number);
            street.setText(home.street);


            delete.setOnClickListener(view -> {
                dbHelper.deleteById(home.id,this, TablesController.Bloki.TABLE_NAME);
                reloadHomes();
            });

            container.addView(itemView);
        }
    }
}