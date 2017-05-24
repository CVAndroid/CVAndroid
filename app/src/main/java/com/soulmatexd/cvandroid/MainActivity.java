package com.soulmatexd.cvandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.soulmatexd.cvandroid.SlidingMain.SlidingScrollListener;
import com.soulmatexd.cvandroid.SlidingMain.SlidingMain;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_sliding_take_photo)
    RoundedImageView mainSlidingTakePhoto;
    @BindView(R.id.main_sliding_album)
    RoundedImageView mainSlidingAlbum;
    @BindView(R.id.main_sliding)
    SlidingMain mainSliding;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;


    private String FILE_PROVIDER;

    private Uri imageUri;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TAKE_PHOTO:
                    takePhoto();
                    break;
                case CHOOSE_PHOTO:
                    chooseFromAlbum();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_main);
        ButterKnife.bind(this);

        FILE_PROVIDER = getResources().getString(R.string.provider_authorities);
        mainSliding.setSlidingScrollListener(new SlidingScrollListener() {
            @Override
            public void onDownPage() {
                chooseFromAlbum();
            }

            @Override
            public void onUpPage() {
                takePhoto();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainSliding.initSliding();
    }

    @OnClick({R.id.main_sliding_take_photo, R.id.main_sliding_album})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.main_sliding_take_photo:
                mainSliding.showTakePhoto();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg1 = new Message();
                        msg1.what = TAKE_PHOTO;
                        handler.sendMessageDelayed(msg1, 500);
                    }
                }).start();

                break;
            case R.id.main_sliding_album:
                mainSliding.showAlbum();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg2 = new Message();
                        msg2.what = CHOOSE_PHOTO;
                        handler.sendMessageDelayed(msg2, 500);
                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                        intent.setData(imageUri);
                        intent.putExtra("TYPE", TAKE_PHOTO);
//                        startActivity(intent);
                    startActivity(new Intent(MainActivity.this,AnalyzeActivity.class));

                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK && data != null) {
                    Uri imageUri = data.getData();
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    intent.setData(imageUri);
                    intent.putExtra("TYPE", CHOOSE_PHOTO);
//                    startActivity(intent);
                startActivity(new Intent(MainActivity.this,AnalyzeActivity.class));
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void chooseFromAlbum() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }

    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOOSE_PHOTO);
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

    public void takePhoto() {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(MainActivity.this, FILE_PROVIDER, outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
}
