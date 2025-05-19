package com.rejner.pomiaryapp;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rejner.pomiaryapp.data.DatabaseController;
import com.rejner.pomiaryapp.data.TablesController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity3 extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoURI;
    private String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        DatabaseController dbHelper = new DatabaseController(this);
//        ImageView imageView = findViewById(R.id.zdjecie_z_bazy); miejsce wyświetlania zdjęcia
        Button captureButton = findViewById(R.id.addPhoto);  //włącza aparat i robisz zdjęcie
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        Set<Integer> selectedImageIds = new HashSet<>();
        LinearLayout imageContainer = findViewById(R.id.imageContainer);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Button deleteButton = findViewById(R.id.deletePhoto);
        Cursor cursor = db.rawQuery("SELECT  " + TablesController.Zdjecia._ID + ", " + TablesController.Zdjecia.COLUMN_NAME_IMAGE + " FROM " + TablesController.Zdjecia.TABLE_NAME, null);
        cursor.getColumnNames();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(TablesController.Zdjecia._ID)); // Image ID
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(TablesController.Zdjecia.COLUMN_NAME_IMAGE));
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                // Create a LinearLayout to hold the ImageView
                LinearLayout itemLayout = new LinearLayout(this);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                // Create the ImageView for the image
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                imageView.setTag(id); // Store the ID in the tag for easy access

                // Set the layout parameters for the image
                LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                        0, 500, 1f); // Image will take up 1/3 of the space
                imageLayoutParams.setMargins(16, 16, 16, 16);
                imageView.setLayoutParams(imageLayoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // Add ImageView to the item layout
                itemLayout.addView(imageView);

                // Add this itemLayout to the image container
                imageContainer.addView(itemLayout);

                // Set OnClickListener for image selection
                imageView.setOnClickListener(view -> {
                    int imageId = (int) view.getTag();

                    if (selectedImageIds.contains(imageId)) {
                        selectedImageIds.remove(imageId); // Deselect the image
                        view.setAlpha(1f); // Reset opacity
                    } else {
                        selectedImageIds.add(imageId); // Select the image
                        view.setAlpha(0.5f); // Make selected images semi-transparent
                    }
                });

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        deleteButton.setOnClickListener(view -> {
            // Check if there are selected images
            if (!selectedImageIds.isEmpty()) {
                for (int imageId : selectedImageIds) {
                    // Remove the image from the layout
                    for (int i = 0; i < imageContainer.getChildCount(); i++) {
                        LinearLayout itemLayout = (LinearLayout) imageContainer.getChildAt(i);
                        ImageView imageView = (ImageView) itemLayout.getChildAt(0); // The first child is the ImageView

                        // If the image ID matches the selected one, remove it from the layout
                        if ((int) imageView.getTag() == imageId) {
                            imageContainer.removeView(itemLayout); // Remove the layout
                            break;
                        }
                    }

                    // Delete the image from the database
                    deleteImageFromDatabase(imageId);
                }

                // Clear the selected images set
                selectedImageIds.clear();
            } else {
                // Optionally show a message if no images are selected
                Toast.makeText(this, "No images selected to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (IOException ex){
                ex.printStackTrace();
            }

            if(photoFile != null){
                photoURI = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() + ".provider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }
        }

    }
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            saveImageToDatabase(bitmap);
        }

    }

    private void saveImageToDatabase(Bitmap bitmap){
        byte[] imageBytes = bitmapToBytes(bitmap);
        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TablesController.Zdjecia.COLUMN_NAME_IMAGE,imageBytes);
        db.insert(TablesController.Zdjecia.TABLE_NAME,null,values);
        db.close();
    }

    private byte[] bitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, stream);
        return stream.toByteArray();

    }
    private void deleteImageFromDatabase(int imageId) {
        DatabaseController dbHelper = new DatabaseController(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{String.valueOf(imageId)};

        // Delete the image from the database
        db.delete(TablesController.Zdjecia.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

}