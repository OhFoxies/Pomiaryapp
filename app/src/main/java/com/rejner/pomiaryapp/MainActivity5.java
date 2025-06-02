package com.rejner.pomiaryapp;

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

import androidx.appcompat.app.AppCompatActivity;

import com.rejner.pomiaryapp.data.DatabaseController;
import com.rejner.pomiaryapp.data.TablesController;

import java.util.List;

public class MainActivity5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        Intent intent = getIntent();
        long foreign_id = intent.getLongExtra("homeID",0);
        String homeName = intent.getStringExtra("homeName");
        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        TextView titleView = findViewById(R.id.textView);
        titleView.setText("Blok: " + homeName);

        this.reloadFlats(foreign_id);
        Button button = findViewById(R.id.createFlat);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText flatNumber = findViewById(R.id.flatNum);
                if (flatNumber.getText().toString().isEmpty()) {
                    TextView errorView = findViewById(R.id.CreationFeedback);
                    errorView.setTextColor(Color.rgb(219, 9, 9));
                    errorView.setText("Nie podano numeru mieszkania");
                } else if (dbHelper.doesFlatExist(flatNumber.getText().toString() , foreign_id)) {
                    TextView errorView = findViewById(R.id.CreationFeedback);
                    errorView.setTextColor(Color.rgb(219, 9, 9));
                    errorView.setText("Mieszkanie z tym numerem juz istnieje");
                } else {
                    ContentValues values = new ContentValues();
                    flatNumber.clearFocus();
                    TextView errorView = findViewById(R.id.CreationFeedback);
                    errorView.setTextColor(Color.rgb(72, 245, 66));
                    errorView.setText("Mieszkanie zostało dodane");

                    values.put(TablesController.Mieszkanie.COLUMN_NAME_NUMBER, flatNumber.getText().toString());
                    values.put(TablesController.Mieszkanie.COLUMN_NAME_HOME_ID, foreign_id);
                    flatNumber.setText("");

                    db.insert(TablesController.Mieszkanie.TABLE_NAME, null, values);
                    reloadFlats(foreign_id);
                }
            }
        });

    }
    private void reloadFlats(long foreign_id) {
        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TablesController.Flat> flats = dbHelper.getAllFlats(foreign_id);
        LinearLayout container = findViewById(R.id.flats);
        LayoutInflater inflater = LayoutInflater.from(this);
        container.removeAllViews();

        for (TablesController.Flat p : flats) {
            View itemView = inflater.inflate(R.layout.item_flats, container, false);

            TextView flatNumber = itemView.findViewById(R.id.flatNum);
            Button button = itemView.findViewById(R.id.showButton);
            Button delete = itemView.findViewById(R.id.deleteButton);

            flatNumber.setText(p.number);
            Log.d("Flat", "Name: " + p.number + ", Home_id: " + p.home_id);

            button.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity3.class);
                intent.putExtra("flat_id", p.id);

                startActivity(intent);
            });




            delete.setOnClickListener(view -> {
                dbHelper.deleteById(p.id,this,TablesController.Mieszkanie.TABLE_NAME);
                reloadFlats(foreign_id);
            });

            container.addView(itemView);
        }
    }

}
