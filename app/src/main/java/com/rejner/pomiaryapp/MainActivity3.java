package com.rejner.pomiaryapp;
import android.content.ContentValues;
import android.content.pm.PackageManager;
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
import java.util.Locale;

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
        ImageView imageView = findViewById(R.id.image_view);
        int imageId = 3;
        Bitmap bitmap = dbHelper.getImageById(imageId);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        } else{
            Toast.makeText(this, "Nie znaleziono zdjecia o ID: " + imageId, Toast.LENGTH_SHORT).show();
        }
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

}