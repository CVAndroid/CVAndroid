package com.soulmatexd.cvandroid;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.soulmatexd.cvandroid.been.CarImageBeen;
import com.soulmatexd.cvandroid.network.ApiService;
import com.soulmatexd.cvandroid.network.SingleRetrofit;
import com.soulmatexd.cvandroid.widgets.SweepView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AnalyzeActivity extends AppCompatActivity{
    private static final String TAG = "AnalyzeActivity";
    File file = new File("/storage/emulated/0/1.jpg");
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    @BindView(R.id.iv_one)
    ImageView car;
    @BindView(R.id.sweep_view)
    SweepView sweepView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: ");
        sweepView.startSweep(car, 15000);

        ObjectAnimator o = ObjectAnimator.ofFloat(car,"alpha",0.6f,0.2f);
        o.setDuration(15000).setInterpolator(new DecelerateInterpolator());
        o.start();

        ApiService apiService = SingleRetrofit.getInstance().create(ApiService.class);

        apiService.getResultCarImage(getImage(file))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CarImageBeen>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.getMessage());
                    }

                    @Override
                    public void onNext(CarImageBeen carImageBeen) {
                        Log.d(TAG, "onNext: "+carImageBeen.getImg());
                    }
                });
    }

    public MultipartBody.Part getImage(File image){
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part photo1part = MultipartBody.Part.createFormData("files", image.getName(), requestBody);
        return photo1part;
    }
    private Bitmap getImageBitmap(){
        Intent intent = getIntent();
        Uri imageUri = intent.getData();
        int type = intent.getIntExtra("TYPE", 3);
        if (type == TAKE_PHOTO){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                Log.d(TAG, "onCreate: "+bitmap.getByteCount());
//                image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return bitmap;
        }else if (type == CHOOSE_PHOTO){
            String imagePath = getImagePath(imageUri, null);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Log.d(TAG, "onCreate: "+bitmap.getByteCount());
            return bitmap;
//            image.setImageBitmap(bitmap);
        }else {
            Intent intent1 = new Intent(AnalyzeActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
            return null;
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
