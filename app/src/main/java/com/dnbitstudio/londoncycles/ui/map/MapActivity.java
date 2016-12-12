package com.dnbitstudio.londoncycles.ui.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.model.BikePoint;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.BaseLocationActivity;
import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends BaseLocationActivity
        implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CAMERA_POSITION = "camera_position";
    private static final String MARKER_LAT_LONG = "marker_lat_long";
    private final String TAG = MapActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private List<BikePoint> mBikePoints = new ArrayList<>();
    private LatLng mLatLng;

    public static void launchActivity(Context context) {
        Intent intent = new Intent(context, MapActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            mCameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION);
            mLatLng = savedInstanceState.getParcelable(MARKER_LAT_LONG);
        }

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putParcelable(CAMERA_POSITION, mMap.getCameraPosition());
        }
        outState.putParcelable(MARKER_LAT_LONG, mLatLng);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_list) {
            BikePointListActivity.launchActivity(this);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLatLong(Location location) {
        super.setLatLong(location);
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        handleNewLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        handleNewLocation();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader");
        return new BikePointCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished");
        parseBikePointsFromCursor(cursor);
        putMarkersInMap();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    private void handleNewLocation() {
        setUpMap();

        if (mCameraPosition == null) {
            mCameraPosition = new CameraPosition.Builder()
                    .target(mLatLng)
                    .zoom(15)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        }
    }

    private void setUpMap() {
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions().position(mLatLng);
        mMap.addMarker(markerOptions);
        putMarkersInMap();
    }

    private void parseBikePointsFromCursor(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            Log.d(TAG, "Table is empty");
            return;
        }

        mBikePoints = new ArrayList<>();

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

            mBikePoints.add(new BikePoint(id, name, lat, lon, docks, empty, bikes));
        } while (cursor.moveToNext());
    }

    private void putMarkersInMap() {
        for (BikePoint bikepoint : mBikePoints) {
            LatLng latLong = new LatLng(bikepoint.getLat(), bikepoint.getLon());
            MarkerOptions markerOptions = new MarkerOptions().position(latLong);
            mMap.addMarker(markerOptions);
        }
    }

    private static class BikePointCursorLoader extends CursorLoader {

        public BikePointCursorLoader(Context context) {
            super(context, BikePointProvider.BIKE_POINTS, null, null, null, null);
        }
    }
}
