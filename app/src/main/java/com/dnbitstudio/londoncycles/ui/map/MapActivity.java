package com.dnbitstudio.londoncycles.ui.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.model.BikePoint;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.BaseLocationActivity;
import com.dnbitstudio.londoncycles.ui.detail.BikePointDetailActivity;
import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;
import com.dnbitstudio.londoncycles.utils.CursorUtils;
import com.dnbitstudio.londoncycles.utils.Utils;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends BaseLocationActivity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CAMERA_POSITION = "camera_position";
    private static final String MARKER_LAT_LONG = "marker_lat_long";
    private final String TAG = MapActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private List<BikePoint> mBikePoints = new ArrayList<>();
    private LatLng mLatLng;
    private BitmapDescriptor mIconMarkerCurrent;
    private BitmapDescriptor mIconMarkerBikePoint;
    private boolean mMarkersInMap = false;

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

        loadIconMarkers();
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
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String bikePointId = (String) marker.getTag();
        BikePointDetailActivity.launchActivity(this, bikePointId);
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
        if (!mMarkersInMap) {
            mBikePoints = CursorUtils.parseBikePointsFromCursor(cursor);
            putMarkersInMap();
        }
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
//        mMap.clear();
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(mLatLng)
//                .icon(mIconMarkerCurrent);
//        mMap.addMarker(markerOptions);
//        putMarkersInMap();
    }

    private void loadIconMarkers() {
        Bitmap iconBitmap = Utils
                .getBitmapFromVectorDrawable(this, R.drawable.ic_current_position_map_marker);
        mIconMarkerCurrent = BitmapDescriptorFactory.fromBitmap(iconBitmap);

        iconBitmap = Utils.getBitmapFromVectorDrawable(this, R.drawable.ic_bikepoint_map_marker);
        mIconMarkerBikePoint = BitmapDescriptorFactory.fromBitmap(iconBitmap);
    }

    private void putMarkersInMap() {
        if (mMarkersInMap) {
            return;
        }

        Log.d(TAG, "init process markers");
        List<MarkerOptions> markerOptionsList = new ArrayList<>();
//        for (BikePoint bikepoint : mBikePoints) {
//            LatLng latLong = new LatLng(bikepoint.getLat(), bikepoint.getLon());
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(latLong)
//                    .title(bikepoint.getName())
//                    .icon(mIconMarkerBikePoint);
//            markerOptionsList.add(markerOptions);
//            Marker marker = mMap.addMarker(markerOptions);
//            marker.setTag(bikepoint.getId());
//            marker.showInfoWindow();
//        }

        new DisplayPinLocationsTask().execute();

//        if (markerOptionsList.size() > 0 ) {
//            mMarkersInMap = true;
//        }
        Log.d(TAG, "finish process markers");
    }

    private static class BikePointCursorLoader extends CursorLoader {

        public BikePointCursorLoader(Context context) {
            super(context, BikePointProvider.BIKE_POINTS, null, null, null, null);
        }
    }

    class DisplayPinLocationsTask extends AsyncTask<Void, Void, Void> {
        public DisplayPinLocationsTask() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (final BikePoint bikepoint : mBikePoints) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LatLng latLong = new LatLng(bikepoint.getLat(), bikepoint.getLon());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLong)
                                .title(bikepoint.getName())
                                .icon(mIconMarkerBikePoint);
                        Marker marker = mMap.addMarker(markerOptions);
                        marker.setTag(bikepoint.getId());
//                        marker.showInfoWindow();
                    }
                });

                // Sleep so we let other UI actions happen in between the markers.
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    // Don't care
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mMarkersInMap = true;
        }
    }
}
