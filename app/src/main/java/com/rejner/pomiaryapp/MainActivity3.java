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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoURI;
    private String currentPhotoPath;
    private Set<Integer> selectedImageIds = new HashSet<>();
    private LinearLayout imageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);

        DatabaseController dbHelper = new DatabaseController(this);

        Button captureButton = findViewById(R.id.addPhoto);
        Button deleteButton = findViewById(R.id.deletePhoto);
        imageContainer = findViewById(R.id.imageContainer);

        captureButton.setOnClickListener(view -> dispatchTakePictureIntent());

        loadImagesFromDatabase(dbHelper);

        deleteButton.setOnClickListener(view -> deleteSelectedImages(dbHelper));
    }

    private void loadImagesFromDatabase(DatabaseController dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TablesController.Zdjecia._ID + ", " +
                TablesController.Zdjecia.COLUMN_NAME_IMAGE + " FROM " + TablesController.Zdjecia.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(TablesController.Zdjecia._ID);
            int imageIndex = cursor.getColumnIndexOrThrow(TablesController.Zdjecia.COLUMN_NAME_IMAGE);

            do {
                int id = cursor.getInt(idIndex);
                byte[] imageBytes = cursor.getBlob(imageIndex);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                if (bitmap != null) {
                    addImageToLayout(bitmap, id);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    private void addImageToLayout(Bitmap bitmap, int id) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        imageView.setTag(id);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                0, 500, 1f
        );
        imageLayoutParams.setMargins(16, 16, 16, 16);
        imageView.setLayoutParams(imageLayoutParams);

        imageView.setOnClickListener(view -> {
            int imageId = (int) view.getTag();
            if (selectedImageIds.contains(imageId)) {
                selectedImageIds.remove(imageId);
                view.setAlpha(1f);
            } else {
                selectedImageIds.add(imageId);
                view.setAlpha(0.5f);
            }
        });

        itemLayout.addView(imageView);
        imageContainer.addView(itemLayout);
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
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap != null) {
                saveImageToDatabase(bitmap);
                addImageToLayout(bitmap, getLastInsertedId());
            } else {
                Toast.makeText(this, "Failed to load captured image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToDatabase(Bitmap bitmap) {
        byte[] imageBytes = bitmapToBytes(bitmap);
        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TablesController.Zdjecia.COLUMN_NAME_IMAGE, imageBytes);
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

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void deleteSelectedImages(DatabaseController dbHelper) {
        if (selectedImageIds.isEmpty()) {
            Toast.makeText(this, "No images selected to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<View> viewsToRemove = new ArrayList<>();

        for (int i = 0; i < imageContainer.getChildCount(); i++) {
            View child = imageContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) child;
                if (layout.getChildAt(0) instanceof ImageView) {
                    ImageView imageView = (ImageView) layout.getChildAt(0);
                    int id = (int) imageView.getTag();
                    if (selectedImageIds.contains(id)) {
                        viewsToRemove.add(layout);

                        // delete from database
                        db.delete(TablesController.Zdjecia.TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
                    }
                }
            }
        }

        for (View v : viewsToRemove) {
            imageContainer.removeView(v);
        }

        selectedImageIds.clear();
        db.close();
    }
}
