package com.example.k4170.picturecompress;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.k4170.picturecompress.utils.FilePathResolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private File demoTemp;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageview);
    }

    public void selectPicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void compressPictureQuality(View view) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(FilePathResolver.getPath(this, uri), options);
        imageView.setImageBitmap(bitmap);
    }

    public void compressPictureSize(View view) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        String path = FilePathResolver.getPath(this, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        }
        BitmapFactory.decodeFile(path, options);
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        options.inSampleSize = outHeight / 300 > outWidth / 300 ? outHeight / 300 : outWidth / 300;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        imageView.setImageBitmap(bitmap);
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        demoTemp = new File(externalStorageDirectory, "temp.jpg");
        uri = Uri.fromFile(demoTemp);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            uri = data.getData();
            Bitmap bitmap = BitmapFactory.decodeFile(FilePathResolver.getPath(this, uri));
            imageView.setImageBitmap(bitmap);
        }
        if (requestCode == 2) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
