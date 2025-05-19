package com.rejner.pomiaryapp;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        Button generuj = findViewById(R.id.generuj);
        LinearLayout layout = findViewById(R.id.main2);
        generuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView test = new TextView(getApplicationContext());
                test.setText("skibidi!");
                layout.addView(test);
            }
        });
    }
}