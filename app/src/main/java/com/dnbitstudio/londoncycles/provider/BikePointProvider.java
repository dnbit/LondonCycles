package com.dnbitstudio.londoncycles.provider;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import novoda.lib.sqliteprovider.provider.SQLiteContentProviderImpl;

public class BikePointProvider extends SQLiteContentProviderImpl {

    public static final String COL_BIKE_POINT_ID = "_id";
    public static final String COL_BIKE_POINT_NAME = "name";
    public static final String COL_BIKE_POINT_LAT = "lat";
    public static final String COL_BIKE_POINT_LON = "lon";
    public static final String COL_BIKE_POINT_DOCKS = "docks";
    public static final String COL_BIKE_POINT_EMPTY = "empty";
    public static final String COL_BIKE_POINT_BIKES = "bikes";
    private static final String AUTHORITY
            = "content://com.dnbitstudio.londoncycles.android.datasync.provider/";
    private static final String TABLE_BIKE_POINT = "bike_point";
    public static final Uri BIKE_POINTS
            = Uri.parse(AUTHORITY).buildUpon().appendPath(TABLE_BIKE_POINT).build();
    public static final Uri CLOSEST_BIKE_POINT
            = Uri.parse(AUTHORITY)
            .buildUpon().appendPath(TABLE_BIKE_POINT)
            .appendQueryParameter("limit", "1").build();

    public static class AllBikePointCursorLoader extends CursorLoader {

        public AllBikePointCursorLoader(Context context) {
            super(context, BikePointProvider.BIKE_POINTS, null, null, null, null);
        }
    }

    public static class ClosestBikePointCursorLoader extends android.support.v4.content.CursorLoader {

        public ClosestBikePointCursorLoader(Context context) {
            super(context, BikePointProvider.CLOSEST_BIKE_POINT, null, null, null, null);
        }
    }

    public static class IdBikePointCursorLoader extends CursorLoader {

        public IdBikePointCursorLoader(Context context, String id) {
            super(context, BikePointProvider.BIKE_POINTS, null,
                    BikePointProvider.COL_BIKE_POINT_ID + " = '" + id + "'", null, null);
        }
    }
}
