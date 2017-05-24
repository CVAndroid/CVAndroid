package com.soulmatexd.cvandroid.network;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xushuzhan on 2017/5/23.
 */

public class SingleRetrofit {
    public static Retrofit getInstance(){
        return SingletonHolder.INSTANCE;
    }
    public static class SingletonHolder{
        public static final Retrofit INSTANCE = new Retrofit.Builder()
                .baseUrl("http://139.199.175.91:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        }
}
