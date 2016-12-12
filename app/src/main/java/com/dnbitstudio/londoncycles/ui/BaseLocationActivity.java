package com.dnbitstudio.londoncycles.ui;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.utils.Utils;

import android.Manifest;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class BaseLocationActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String LOCATION_SHARED_PREFERENCES = "location_shared_preferences";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final String TAG = BaseLocationActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public void onConnected(@Nullable Bundle bundle) {
        Utils.isGPSEnabled(this);
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(getApplicationContext(), R.string.network_unavailable,
                    Toast.LENGTH_SHORT).show();
        } else {
            BaseLocationActivityPermissionsDispatcher.performOnConnectedTasksWithCheck(this);
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        BaseLocationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void performOnConnectedTasks() {
        Log.d(TAG, "Location services connected");
        @SuppressWarnings("MissingPermission")
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        if (location == null) {
            // request location updates and let the location listener handle the updates
            //noinspection MissingPermission
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            // use the location
            setLatLong(location);
        }
    }

    public void setLatLong(Location location) {
        Utils.saveLatLonInSharedPreferences(this, location.getLatitude(), location.getLongitude());
    }

    protected synchronized void buildGoogleApiClient() {
        // Initialize our GoogleAPIClient object
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
