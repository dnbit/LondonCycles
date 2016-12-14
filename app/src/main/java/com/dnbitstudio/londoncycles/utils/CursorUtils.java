package com.dnbitstudio.londoncycles.utils;

import com.dnbitstudio.londoncycles.model.BikePoint;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CursorUtils {

    private static final String TAG = BikePointListActivity.class.getSimpleName();

    public static List<BikePoint> parseBikePointsFromCursor(Cursor cursor) {
        List<BikePoint> bikePoints = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(
                        cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_ID));
                String name = cursor.getString(
                        cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_NAME));
                double lat = cursor.getDouble(
                        cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_LAT));
                double lon = cursor.getDouble(
                        cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_LON));
                int docks = cursor.getInt(
                        cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_DOCKS));
                int empty = cursor.getInt(
                        cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_EMPTY));
                int bikes = cursor.getInt(
                        cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_BIKES));

                bikePoints.add(new BikePoint(id, name, lat, lon, docks, empty, bikes));
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "Table is empty");
        }

        return bikePoints;
    }
}
