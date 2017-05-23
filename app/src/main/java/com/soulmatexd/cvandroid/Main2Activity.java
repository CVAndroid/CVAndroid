package com.soulmatexd.cvandroid;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.makeramen.roundedimageview.RoundedImageView;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity {

    @BindView(R.id.main_2_image)
    RoundedImageView image;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private static final String TAG = "Main2Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_2_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Uri imageUri = intent.getData();
        int type = intent.getIntExtra("TYPE", 3);
        if (type == TAKE_PHOTO){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                Log.d(TAG, "onCreate: "+bitmap.getByteCount());
                image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (type == CHOOSE_PHOTO){
            String imagePath = getImagePath(imageUri, null);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Log.d(TAG, "onCreate: "+bitmap.getByteCount());

            image.setImageBitmap(bitmap);
        }else {
            Intent intent1 = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
