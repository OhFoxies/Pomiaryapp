package com.rejner.pomiaryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.rejner.pomiaryapp.data.DatabaseController;
import com.rejner.pomiaryapp.data.TablesController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity3 extends AppCompatActivity {

    // Constants and fields for photo capture and image management
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoURI;
    private String currentPhotoPath;
    private Set<Integer> selectedImageIds = new HashSet<>();
    private LinearLayout imageContainer;  // For Main3 images

    // Fields for Main6 functionality (rooms)
    private LinearLayout pokoje;
    private Button addRoomButton;
    private RadioGroup roomTypeGroup;
    private int textViewCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3); // or a combined layout if you have one

        // Initialize views for Main3 section
        Button captureButton = findViewById(R.id.addPhoto_main3);
        Button deleteButton = findViewById(R.id.deletePhoto_main3);
        imageContainer = findViewById(R.id.imageContainer_main3);

        // Initialize views for Main6 section
        pokoje = findViewById(R.id.Pokoje_main6);
        addRoomButton = findViewById(R.id.addRoom_main6);
        roomTypeGroup = findViewById(R.id.roomTypeGroup_main6);

        // Set up Main3 listeners
        captureButton.setOnClickListener(view -> dispatchTakePictureIntent());
        deleteButton.setOnClickListener(view -> deleteSelectedImages(new DatabaseController(this)));
        loadImagesFromDatabase(new DatabaseController(this));

        // Set up Main6 listeners
        addRoomButton.setOnClickListener(view -> addRoom());
    }

    // ----- Main3 Methods (Image capture and display) -----

    private void loadImagesFromDatabase(DatabaseController dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TablesController.Zdjecia._ID + ", " +
                TablesController.Zdjecia.COLUMN_NAME_IMAGE + " FROM " + TablesController.Zdjecia.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(TablesController.Zdjecia._ID);
            int imageIndex = cursor.getColumnIndexOrThrow(TablesController.Zdjecia.COLUMN_NAME_IMAGE);

            do {
                int id = cursor.getInt(idIndex);
                String imagePath = cursor.getString(imageIndex);

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap != null) {
                    addImageToLayout(bitmap, id);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    private void addImageToLayout(Bitmap bitmap, int id) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        imageView.setTag(id);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 500
        );
        imageLayoutParams.setMargins(16, 16, 16, 16);
        imageView.setLayoutParams(imageLayoutParams);

        // Tap to view full image
        imageView.setOnClickListener(view -> showFullImage(bitmap));

        // Long press to select/deselect for deletion
        imageView.setOnLongClickListener(view -> {
            int imageId = (int) view.getTag();
            if (selectedImageIds.contains(imageId)) {
                selectedImageIds.remove(imageId);
                view.setAlpha(1f);
            } else {
                selectedImageIds.add(imageId);
                view.setAlpha(0.5f);
            }
            return true;
        });

        imageContainer.addView(imageView);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (currentPhotoPath != null) {
                saveImageToDatabase(currentPhotoPath);
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                if (bitmap != null) {
                    addImageToLayout(bitmap, getLastInsertedId());
                } else {
                    Toast.makeText(this, "Failed to load captured image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveImageToDatabase(String imagePath) {
        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TablesController.Zdjecia.COLUMN_NAME_IMAGE, imagePath);
        db.insert(TablesController.Zdjecia.TABLE_NAME, null, values);
        db.close();
    }

    private int getLastInsertedId() {
        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }

    private void deleteSelectedImages(DatabaseController dbHelper) {
        if (selectedImageIds.isEmpty()) {
            Toast.makeText(this, "No images selected to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<View> viewsToRemove = new ArrayList<>();

        for (int i = 0; i < imageContainer.getChildCount(); i++) {
            View view = imageContainer.getChildAt(i);
            if (view instanceof ImageView) {
                int id = (int) view.getTag();
                if (selectedImageIds.contains(id)) {
                    // Fetch path before deleting DB row
                    String imagePath = getImagePathById(db, id);
                    if (imagePath != null) {
                        File file = new File(imagePath);
                        if (file.exists()) {
                            boolean deleted = file.delete();
                            if (!deleted) {
                                Log.w("MainActivityMerged", "Failed to delete file: " + imagePath);
                            }
                        }
                    }
                    // Delete from DB
                    db.delete(TablesController.Zdjecia.TABLE_NAME, TablesController.Zdjecia._ID + "=?", new String[]{String.valueOf(id)});
                    viewsToRemove.add(view);
                }
            }
        }

        for (View view : viewsToRemove) {
            imageContainer.removeView(view);
        }

        selectedImageIds.clear();
        db.close();
    }

    private String getImagePathById(SQLiteDatabase db, int id) {
        String imagePath = null;
        Cursor cursor = db.query(
                TablesController.Zdjecia.TABLE_NAME,
                new String[]{TablesController.Zdjecia.COLUMN_NAME_IMAGE},
                TablesController.Zdjecia._ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(TablesController.Zdjecia.COLUMN_NAME_IMAGE));
            }
            cursor.close();
        }
        return imagePath;
    }

    private void showFullImage(Bitmap bitmap) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        ImageView fullImageView = new ImageView(this);
        fullImageView.setImageBitmap(bitmap);
        fullImageView.setAdjustViewBounds(true);
        builder.setView(fullImageView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // ----- Main6 Methods (Rooms with sockets etc) -----

    private void addRoom() {
        // Show Pokoje_main6 container if hidden
        if (pokoje.getVisibility() == View.GONE) {
            pokoje.setVisibility(View.VISIBLE);
        }

        textViewCount++;

        // Get selected room type from RadioGroup
        int selectedId = roomTypeGroup.getCheckedRadioButtonId();
        String roomType = "Pokój"; // default
        if (selectedId == R.id.radioKuchnia_main6) {
            roomType = "Kuchnia";
        } else if (selectedId == R.id.radioLazienka_main6) {
            roomType = "Łazienka";
        } else if (selectedId == R.id.radioPokoj_main6) {
            roomType = "Pokój";
        }

        // Create container for the room (vertical)
        LinearLayout roomLayout = new LinearLayout(this);
        roomLayout.setOrientation(LinearLayout.VERTICAL);
        roomLayout.setPadding(0, 20, 0, 20);

        // Add horizontal black line separator if this is not the first room
        if (pokoje.getChildCount() > 0) {
            View separator = new View(this);
            separator.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2));
            separator.setBackgroundColor(0xFF000000); // black line
            pokoje.addView(separator);
        }

        // Title layout with room name and delete button
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setPadding(0, 10, 0, 10);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView roomTitleTextView = new TextView(this);
        roomTitleTextView.setText(roomType + " " + textViewCount);
        roomTitleTextView.setTextSize(18);
        roomTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)); // fill remaining space
        titleLayout.addView(roomTitleTextView);

        Button deleteRoomButton = new Button(this);
        deleteRoomButton.setText("Usuń");
        deleteRoomButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        titleLayout.addView(deleteRoomButton);

        roomLayout.addView(titleLayout);

        // Container for sockets: vertical layout
        LinearLayout socketsContainer = new LinearLayout(this);
        socketsContainer.setOrientation(LinearLayout.VERTICAL);
        socketsContainer.setPadding(10, 10, 10, 10);
        roomLayout.addView(socketsContainer);

        // EditText to input new socket info
        EditText socketInputEditText = new EditText(this);
        socketInputEditText.setHint("Wpisz informacje o gniazdku");
        roomLayout.addView(socketInputEditText);

        // Button to add socket info
        Button addSocketButton = new Button(this);
        addSocketButton.setText("Dodaj gniazdko");
        roomLayout.addView(addSocketButton);

        pokoje.addView(roomLayout);

        // Keep track of socket count per room
        final int[] socketCount = {0};

        // Add socket button logic
        addSocketButton.setOnClickListener(v -> {
            String socketInfo = socketInputEditText.getText().toString().trim();
            if (socketInfo.isEmpty()) {
                Toast.makeText(this, "Wpisz informacje o gniazdku", Toast.LENGTH_SHORT).show();
                return;
            }

            socketCount[0]++;
            String label = "Gniazdko " + socketCount[0] + ": " + socketInfo;

            // Create a horizontal layout for this socket line + delete button
            LinearLayout socketLineLayout = new LinearLayout(this);
            socketLineLayout.setOrientation(LinearLayout.HORIZONTAL);
            socketLineLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            socketLineLayout.setPadding(0, 5, 0, 5);

            // TextView for socket info
            TextView socketTextView = new TextView(this);
            socketTextView.setText(label);
            socketTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            socketLineLayout.addView(socketTextView);

            // Delete button for this socket
            Button deleteSocketButton = new Button(this);
            deleteSocketButton.setText("X");
            deleteSocketButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            socketLineLayout.addView(deleteSocketButton);

            // Add socket line layout to sockets container
            socketsContainer.addView(socketLineLayout);

            // Clear input
            socketInputEditText.setText("");

            // Delete socket button logic
            deleteSocketButton.setOnClickListener(delView -> {
                socketsContainer.removeView(socketLineLayout);
                // Optional: Adjust socket numbering if you want
                // For simplicity, we won't re-number sockets here
            });
        });

        // Delete room button logic
        deleteRoomButton.setOnClickListener(v -> {
            pokoje.removeView(roomLayout);
            // Hide container if no rooms remain
            if (pokoje.getChildCount() == 0) {
                pokoje.setVisibility(View.GONE);
            }
        });
    }
    private void appendSocketInfo(TextView socketsTextView, String newSocketData) {
        String currentText = socketsTextView.getText().toString().trim();
        int count = 0;
        if (!currentText.isEmpty()) {
            // Count existing lines to number the new socket correctly
            count = currentText.split("\n").length;
        }
        int newSocketNumber = count + 1;
        String appendedText = currentText.isEmpty() ?
                "Gniazdko " + newSocketNumber + ": " + newSocketData :
                currentText + "\nGniazdko " + newSocketNumber + ": " + newSocketData;
        socketsTextView.setText(appendedText);
    }

    private void showSocketInfoDialog(TextView gniazdkoInfoTextView) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Dodaj gniazdko");

        // Create layout for dialog inputs
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);

        EditText editTextTypGniazdka = new EditText(this);
        editTextTypGniazdka.setHint("Typ gniazdka");
        layout.addView(editTextTypGniazdka);

        EditText editTextLiczba = new EditText(this);
        editTextLiczba.setHint("Liczba");
        editTextLiczba.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(editTextLiczba);

        builder.setView(layout);

        builder.setPositiveButton("Dodaj", (dialog, which) -> {
            String typGniazdka = editTextTypGniazdka.getText().toString().trim();
            String liczbaStr = editTextLiczba.getText().toString().trim();

            if (!typGniazdka.isEmpty() && !liczbaStr.isEmpty()) {
                try {
                    int liczba = Integer.parseInt(liczbaStr);
                    String existingText = gniazdkoInfoTextView.getText().toString();
                    String newText = existingText + typGniazdka + ": " + liczba + "\n";
                    gniazdkoInfoTextView.setText(newText);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Liczba musi być liczbą całkowitą", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Wypełnij oba pola", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
