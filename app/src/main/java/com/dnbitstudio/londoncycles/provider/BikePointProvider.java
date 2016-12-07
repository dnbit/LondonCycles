package com.dnbitstudio.londoncycles.provider;

import android.net.Uri;

import novoda.lib.sqliteprovider.provider.SQLiteContentProviderImpl;

public class BikePointProvider extends SQLiteContentProviderImpl {

    public static final String COL_BIKE_POINT_ID = "id";
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
}
