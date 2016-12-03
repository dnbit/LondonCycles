package com.dnbitstudio.londoncycles.ui.map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.dnbitstudio.londoncycles.BuildConfig;
import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.model.AdditionalProperty;
import com.dnbitstudio.londoncycles.model.BikePoint;
import com.dnbitstudio.londoncycles.model.TflService;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;
import com.dnbitstudio.londoncycles.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String CAMERA_POSITION = "camera_position";
    private static final String MARKER_LAT_LONG = "marker_lat_long";
    private final String TAG = MapActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng mLatLng;
    private CameraPosition mCameraPosition;
    private List<BikePoint> mBikePoints;

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

        buildGoogleApiClient();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CAMERA_POSITION, mMap.getCameraPosition());
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Utils.isGPSEnabled(this);
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(getApplicationContext(), R.string.network_unavailable,
                    Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Location services connected");
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                // request location updates and let the location listener handle the updates
                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                // use the location
                setLatLong(location);
                handleNewLocation();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Location services disconnected");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.d(TAG, "Connection Failed " + e.getMessage());
            }
        } else {
            Log.d(TAG, "Location services connection failed with code " +
                    connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "location changed");
        setLatLong(location);
        handleNewLocation();
    }

    private void setLatLong(Location location) {
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void handleNewLocation() {
        setUpMap();

        if (mCameraPosition == null) {
            mCameraPosition = new CameraPosition.Builder()
                    .target(mLatLng)
                    .zoom(15)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
            callApi();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        // Initialize our GoogleAPIClient object
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setUpMap() {
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions().position(mLatLng);
        mMap.addMarker(markerOptions);
    }

    private void callApi() {
        String mockedJson = "";
        if (BuildConfig.DEBUG) {
            mockedJson = Utils.loadMockedBikePoints(this);
        }

        TflService tflService = new TflService(mockedJson);

        tflService.loadBikePoints(new Callback<List<BikePoint>>() {
            @Override
            public void onResponse(Call<List<BikePoint>> call, Response<List<BikePoint>> response) {
                Log.d(TAG, "onResponse");
                mBikePoints = response.body();
                putMarkersInMap();
                saveBikePointsToDatabase();
            }

            @Override
            public void onFailure(Call<List<BikePoint>> call, Throwable t) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    private void putMarkersInMap() {
        for (BikePoint bikepoint : mBikePoints) {
            LatLng latLong = new LatLng(bikepoint.getLat(), bikepoint.getLon());
            MarkerOptions markerOptions = new MarkerOptions().position(latLong);
            mMap.addMarker(markerOptions);
        }
    }

    private void saveBikePointsToDatabase() {
        List<ContentValues> contentValues = new ArrayList<>();
        ContentValues values;
        for (BikePoint bikePoint : mBikePoints) {
            values = new ContentValues(1);
            values.put(BikePointProvider.COL_BIKE_POINT_ID, bikePoint.getId());
            values.put(BikePointProvider.COL_BIKE_POINT_NAME, bikePoint.getCommonName());
            values.put(BikePointProvider.COL_BIKE_POINT_LATITUDE, bikePoint.getLat());
            values.put(BikePointProvider.COL_BIKE_POINT_LONGITUDE, bikePoint.getLon());

            List<AdditionalProperty> additionalProperties = bikePoint.getAdditionalProperties();
            for (AdditionalProperty additionalProperty : additionalProperties) {
                switch (additionalProperty.getKey()) {
                    case "NbDocks":
                        values.put(BikePointProvider.COL_BIKE_POINT_DOCKS,
                                additionalProperty.getValue());
                        break;
                    case "NbEmptyDocks":
                        values.put(BikePointProvider.COL_BIKE_POINT_EMPTY,
                                additionalProperty.getValue());
                        break;
                    case "NbBikes":
                        values.put(BikePointProvider.COL_BIKE_POINT_BIKES,
                                additionalProperty.getValue());
                        break;
                }
            }

            contentValues.add(values);
        }

        Uri table = BikePointProvider.BIKE_POINTS;
        ContentValues[] bulk = new ContentValues[contentValues.size()];
        contentValues.toArray(bulk);

        getContentResolver().bulkInsert(table, bulk);
    }

    private void retrieveBikePointsFromDatabase() {
        getSupportLoaderManager().initLoader(R.id.loader_bike_points, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {

                    @Override
                    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                        return new BikePointCursorLoader(getApplicationContext());
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
                        if (!cursor.moveToFirst()) {
                            Log.d(TAG, "Table is empty");
                        }

                        do {
                            String id = cursor.getString(cursor.getColumnIndex("id"));
                            String name = cursor.getString(cursor.getColumnIndex("name"));
                            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                            int docks = cursor.getInt(cursor.getColumnIndex("docks"));
                            int empty = cursor.getInt(cursor.getColumnIndex("empty"));
                            int bikes = cursor.getInt(cursor.getColumnIndex("bikes"));

                            Log.d(TAG, "Found id: " + id);
                            Log.d(TAG, "Found name: " + name);
                            Log.d(TAG, "Found latitude: " + latitude);
                            Log.d(TAG, "Found longitude: " + longitude);
                            Log.d(TAG, "Found docks: " + docks);
                            Log.d(TAG, "Found empty: " + empty);
                            Log.d(TAG, "Found bikes: " + bikes);
                        } while (cursor.moveToNext());
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> cursorLoader) {

                    }
                });
    }

    private static class BikePointCursorLoader extends CursorLoader {

        public BikePointCursorLoader(Context context) {
            super(context, BikePointProvider.BIKE_POINTS, null, null, null, null);
        }
    }
}
