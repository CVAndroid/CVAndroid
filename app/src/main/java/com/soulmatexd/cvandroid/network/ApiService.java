package com.soulmatexd.cvandroid.network;

import com.soulmatexd.cvandroid.been.MovieBeen;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by xushuzhan on 2017/5/23.
 */

public interface ApiService {
    //测试数据
    @GET("top250")
    Observable<MovieBeen> getMovie(@Query("start") int start, @Query("count") int count);
}
