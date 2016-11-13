package com.dnbitstudio.londoncycles.network;

import com.dnbitstudio.londoncycles.model.BikePoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TflApi {
//    @GET("/BikePoint")
//    Call<List<BikePoint>> loadBikePoints(@Query("id") String id,
//                                    @Query("app_key") String appKey);

    @GET("/bikepoint")
    Call<List<BikePoint>> loadBikePoints();
}
