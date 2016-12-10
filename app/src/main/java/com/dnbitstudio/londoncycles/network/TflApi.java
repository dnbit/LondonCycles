package com.dnbitstudio.londoncycles.network;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TflApi {
    // https://api.tfl.gov.uk/BikePoint?app_id=<your app id>&app_key=<your app key>

    @GET("/BikePoint")
    Call<JsonArray> loadBikePoints(@Query("app_id") String appId,
                                   @Query("app_key") String appKey);

//    @GET("/BikePoint/{id}")
//    Call<List<BikePoint>> loadBikePointDetails(@Path("id") String bikePointId,
//                                               @Query("app_id") String appId,
//                                               @Query("app_key") String appKey);

//    @GET("/BikePoint")
//    Call<JsonArray> loadBikePoints();

//
//    @GET("/BikePoint/{id}")
//    Call<BikePoint> loadBikePointDetails(@Path("id") String bikePointId);
}
