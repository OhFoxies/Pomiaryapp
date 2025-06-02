package com.rejner.pomiaryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity6 extends AppCompatActivity {
    private LinearLayout pokoje;
    private Button addRoomButton;
    private RadioGroup roomTypeGroup;
    private int textViewCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main6);

        pokoje = findViewById(R.id.Pokoje);
        addRoomButton = findViewById(R.id.addRoom);
        roomTypeGroup = findViewById(R.id.roomTypeGroup);

        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewCount++;

                // Get selected room type from RadioGroup
                int selectedId = roomTypeGroup.getCheckedRadioButtonId();
                String roomType = "Pokój"; // default
                if (selectedId == R.id.radioKuchnia) {
                    roomType = "Kuchnia";
                } else if (selectedId == R.id.radioLazienka) {
                    roomType = "Łazienka";
                } else if (selectedId == R.id.radioPokoj) {
                    roomType = "Pokój";
                }

                // Container layout for this pokój
                LinearLayout roomLayout = new LinearLayout(MainActivity6.this);
                roomLayout.setOrientation(LinearLayout.VERTICAL);
                roomLayout.setPadding(0, 20, 0, 20);

                // Horizontal layout for title + delete button
                LinearLayout titleLayout = new LinearLayout(MainActivity6.this);
                titleLayout.setOrientation(LinearLayout.HORIZONTAL);
                titleLayout.setPadding(0, 10, 0, 10);
                titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // 1. TextView for room title + number
                TextView roomTitleTextView = new TextView(MainActivity6.this);
                roomTitleTextView.setText(roomType + " " + textViewCount);
                roomTitleTextView.setTextSize(18);
                roomTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)); // take all space but leave room for button
                titleLayout.addView(roomTitleTextView);

                // 2. Delete button for this room
                Button deleteButton = new Button(MainActivity6.this);
                deleteButton.setText("Usuń");
                deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                titleLayout.addView(deleteButton);

                roomLayout.addView(titleLayout);

                // 3. TextView to accumulate gniazdko info
                TextView gniazdkoInfoTextView = new TextView(MainActivity6.this);
                gniazdkoInfoTextView.setTextSize(16);
                gniazdkoInfoTextView.setPadding(0, 10, 0, 10);
                roomLayout.addView(gniazdkoInfoTextView);

                // 4. EditText for gniazdko input
                EditText gniazdkoInput = new EditText(MainActivity6.this);
                gniazdkoInput.setHint("Wpisz dane gniazdka");
                roomLayout.addView(gniazdkoInput);

                // 5. Button to add gniazdko info
                Button addGniazdkoBtn = new Button(MainActivity6.this);
                addGniazdkoBtn.setText("Dodaj Gniazdko");
                roomLayout.addView(addGniazdkoBtn);

                final int[] gniazdkoCountForThisRoom = {0};

                addGniazdkoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputText = gniazdkoInput.getText().toString().trim();
                        if (!inputText.isEmpty()) {
                            gniazdkoCountForThisRoom[0]++;
                            String currentText = gniazdkoInfoTextView.getText().toString();
                            String newLine = "Gniazdko " + gniazdkoCountForThisRoom[0] + ": " + inputText;
                            if (!currentText.isEmpty()) {
                                newLine = "\n" + newLine; // add newline if text exists
                            }
                            gniazdkoInfoTextView.append(newLine);
                            gniazdkoInput.setText("");
                        }
                    }
                });

                // Delete button removes this whole pokój view
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pokoje.removeView(roomLayout);
                    }
                });

                // Add this pokój container to the main layout
                pokoje.addView(roomLayout);
            }
        });
    }
}