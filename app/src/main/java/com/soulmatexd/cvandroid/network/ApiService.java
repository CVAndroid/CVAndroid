package com.soulmatexd.cvandroid.network;

import com.soulmatexd.cvandroid.been.CarImageBeen;

import okhttp3.MultipartBody;

import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by xushuzhan on 2017/5/23.
 */

public interface ApiService {
    @Multipart
    @POST("car/upload/")
    Observable<CarImageBeen> getResultCarImage(@Part MultipartBody.Part file);

}
