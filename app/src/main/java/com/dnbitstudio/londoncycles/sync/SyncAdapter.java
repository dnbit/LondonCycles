package com.dnbitstudio.londoncycles.sync;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.model.BikePoint;
import com.dnbitstudio.londoncycles.model.TflService;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.BaseLocationActivity;
import com.dnbitstudio.londoncycles.utils.LocationDistanceComparator;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACTION_DATA_UPDATED
            = "com.dnbitstudio.londoncycles.app.ACTION_DATA_UPDATED";
    private static final String BIKE_POINT_PARSE_ID = "id";
    private static final String BIKE_POINT_PARSE_NAME = "commonName";
    private static final String BIKE_POINT_PARSE_LAT = "lat";
    private static final String BIKE_POINT_PARSE_LON = "lon";
    private static final String BIKE_POINT_PARSE_ADDITIONAL_PROPERTIES = "additionalProperties";
    private static final String BIKE_POINT_PARSE_KEY = "key";
    private static final String BIKE_POINT_PARSE_VALUE = "value";
    private final String TAG = SyncAdapter.class.getSimpleName();
    // Global variables
    // Define a variable to contain a content resolver instance
    private ContentResolver mContentResolver;
    private List<BikePoint> mBikePoints;
    private BikePoint mBikePoint;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync");
        callApi();
    }

    private void callApi() {
        Log.d(TAG, "CallApi");
        String mockedJson = "";
//        if (BuildConfig.DEBUG) {
//            mockedJson = Utils.loadMockedBikePoints(getContext());
//        }

        String appId = getContext().getString(R.string.tfl_app_id);
        String appKey = getContext().getString(R.string.tfl_app_key);

        TflService tflService = new TflService(mockedJson);
        tflService.loadBikePoints(appId, appKey, new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d(TAG, "onResponse");
                mBikePoints = new ArrayList<>();

                JsonArray bikePointJsonArray = response.body();
                parseBikePointJsonArray(bikePointJsonArray);

                saveBikePointsToDatabase();
                broadcastUpdate();
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.d(TAG, "onFailure" + t.getMessage());
            }
        });
    }

    private void parseBikePointJsonArray(JsonArray bikePointJsonArray) {
        Log.d(TAG, "parseBikePoints");
        for (JsonElement bikePointElement : bikePointJsonArray) {
            mBikePoint = new BikePoint();
            JsonObject bikePointJsonObject = bikePointElement.getAsJsonObject();

            mBikePoint.setId(bikePointJsonObject.get(BIKE_POINT_PARSE_ID).getAsString());
            mBikePoint.setName(bikePointJsonObject.get(BIKE_POINT_PARSE_NAME).getAsString());
            mBikePoint.setLat(bikePointJsonObject.get(BIKE_POINT_PARSE_LAT).getAsDouble());
            mBikePoint.setLon(bikePointJsonObject.get(BIKE_POINT_PARSE_LON).getAsDouble());

            JsonArray additionalPropertiesJsonArray
                    = bikePointJsonObject.getAsJsonArray(BIKE_POINT_PARSE_ADDITIONAL_PROPERTIES);
            parseAdditionalPropertiesJsonArray(additionalPropertiesJsonArray);
            mBikePoints.add(mBikePoint);
        }
    }

    private void parseAdditionalPropertiesJsonArray(JsonArray additionalPropertiesJsonArray) {
        for (JsonElement additionalPropertyElement : additionalPropertiesJsonArray) {
            JsonObject additionalPropertyJsonObject
                    = additionalPropertyElement.getAsJsonObject();

            String key = additionalPropertyJsonObject.get(BIKE_POINT_PARSE_KEY).getAsString();
            switch (key) {
                case "NbBikes":
                    mBikePoint.setBikes(
                            additionalPropertyJsonObject.get(BIKE_POINT_PARSE_VALUE).getAsInt());
                    break;
                case "NbDocks":
                    mBikePoint.setDocks(
                            additionalPropertyJsonObject.get(BIKE_POINT_PARSE_VALUE).getAsInt());
                    break;
                case "NbEmptyDocks":
                    mBikePoint.setEmpty(
                            additionalPropertyJsonObject.get(BIKE_POINT_PARSE_VALUE).getAsInt());
                    break;
            }
        }
    }

    private void saveBikePointsToDatabase() {
        sortBikePointsByDistance();

        Log.d(TAG, "saveBikePointsToDatabase");
        List<ContentValues> contentValues = new ArrayList<>();
        ContentValues values;
        for (BikePoint bikePoint : mBikePoints) {
            values = new ContentValues(1);
            values.put(BikePointProvider.COL_BIKE_POINT_ID, bikePoint.getId());
            values.put(BikePointProvider.COL_BIKE_POINT_NAME, bikePoint.getName());
            values.put(BikePointProvider.COL_BIKE_POINT_LAT, bikePoint.getLat());
            values.put(BikePointProvider.COL_BIKE_POINT_LON, bikePoint.getLon());
            values.put(BikePointProvider.COL_BIKE_POINT_DOCKS, bikePoint.getDocks());
            values.put(BikePointProvider.COL_BIKE_POINT_EMPTY, bikePoint.getEmpty());
            values.put(BikePointProvider.COL_BIKE_POINT_BIKES, bikePoint.getBikes());

            contentValues.add(values);
        }

        Uri table = BikePointProvider.BIKE_POINTS;
        ContentValues[] bulk = new ContentValues[contentValues.size()];
        contentValues.toArray(bulk);

        mContentResolver.delete(table, null, null);
        mContentResolver.bulkInsert(table, bulk);
    }

    private void sortBikePointsByDistance() {
        SharedPreferences sharedPrefs = getContext().getSharedPreferences(
                BaseLocationActivity.LOCATION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        double latitude = Double.valueOf(
                sharedPrefs.getString(BaseLocationActivity.KEY_LATITUDE, "0"));
        double longitude = Double.valueOf(
                sharedPrefs.getString(BaseLocationActivity.KEY_LONGITUDE, "0"));

        Collections.sort(mBikePoints, new LocationDistanceComparator(latitude, longitude));
    }

    private void broadcastUpdate() {
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        getContext().sendBroadcast(dataUpdatedIntent);
    }
}
