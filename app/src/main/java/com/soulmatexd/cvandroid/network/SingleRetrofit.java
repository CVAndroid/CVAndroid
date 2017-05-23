package com.soulmatexd.cvandroid.network;

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
                .baseUrl("https://api.douban.com/v2/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        }
}
