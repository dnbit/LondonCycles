package com.dnbitstudio.londoncycles.ui;


import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    /**
     * See /assets/migrations/1_SETUP.SQL for the database creation
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * You can save multiple ways - this is just an example of using Uri's
         * do not normally do this on the UI Thread
         */
        saveNewBikePointToDatabase();
        /**
         * You can retrieve from the database multiple ways - this is just an example of using Uri's
         */
        retrieveBikePointsFromDatabase();
    }

    private void saveNewBikePointToDatabase() {
        Uri table = BikePointProvider.BIKE_POINTS;
        ContentValues values = new ContentValues(1);
        values.put(BikePointProvider.COL_BIKE_POINT_ID, "BikePoints_1");
        values.put(BikePointProvider.COL_BIKE_POINT_NAME, "River Street , Clerkenwell");
        values.put(BikePointProvider.COL_BIKE_POINT_LATITUDE, 51.529163);
        values.put(BikePointProvider.COL_BIKE_POINT_LONGITUDE, -0.10997);
        values.put(BikePointProvider.COL_BIKE_POINT_DOCKS, 20);
        values.put(BikePointProvider.COL_BIKE_POINT_EMPTY, 12);
        values.put(BikePointProvider.COL_BIKE_POINT_BIKES, 8);

        getActivity().getContentResolver().insert(table, values);
    }

    private void retrieveBikePointsFromDatabase() {
        getActivity().getSupportLoaderManager()
                .initLoader(R.id.loader_bike_points, null, new LoaderManager.LoaderCallbacks<Cursor>() {

                    @Override
                    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                        return new BikePointCursorLoader(getActivity());
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
                        if (!cursor.moveToFirst()) {
                            Log.d("demo", "Nothing in DB, returning early");
                        }

                        do {
                            String id = cursor.getString(cursor.getColumnIndex("id"));
                            String name = cursor.getString(cursor.getColumnIndex("name"));
                            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                            int docks = cursor.getInt(cursor.getColumnIndex("docks"));
                            int empty = cursor.getInt(cursor.getColumnIndex("empty"));
                            int bikes = cursor.getInt(cursor.getColumnIndex("bikes"));

                            Log.d("demo", "Found id: " + id);
                            Log.d("demo", "Found name: " + name);
                            Log.d("demo", "Found latitude: " + latitude);
                            Log.d("demo", "Found longitude: " + longitude);
                            Log.d("demo", "Found docks: " + docks);
                            Log.d("demo", "Found empty: " + empty);
                            Log.d("demo", "Found bikes: " + bikes);
                        } while (cursor.moveToNext());

                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> cursorLoader) {

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private static class BikePointCursorLoader extends CursorLoader {

        public BikePointCursorLoader(Context context) {
            super(context, BikePointProvider.BIKE_POINTS, null, null, null, null);
        }
    }
}
