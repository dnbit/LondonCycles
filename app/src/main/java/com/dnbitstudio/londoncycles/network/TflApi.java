package com.dnbitstudio.londoncycles.network;

import com.dnbitstudio.londoncycles.model.BikePoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TflApi {
//    @GET("/BikePoint")
//    Call<List<BikePoint>> loadBikePoints(@Query("app_id") String appId,
//                                    @Query("app_key") String appKey);

//    @GET("/BikePoint/{id}")
//    Call<List<BikePoint>> loadBikePointDetails(@Path("id") String bikePointId,
//                                               @Query("app_id") String appId,
//                                               @Query("app_key") String appKey);

    @GET("/BikePoint")
    Call<List<BikePoint>> loadBikePoints();
//
//    @GET("/BikePoint/{id}")
//    Call<BikePoint> loadBikePointDetails(@Path("id") String bikePointId);
}
