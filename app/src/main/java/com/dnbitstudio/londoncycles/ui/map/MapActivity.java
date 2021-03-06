package com.dnbitstudio.londoncycles.ui.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.model.BikePoint;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.BaseLocationActivity;
import com.dnbitstudio.londoncycles.ui.detail.BikePointDetailActivity;
import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;
import com.dnbitstudio.londoncycles.utils.CursorUtils;
import com.dnbitstudio.londoncycles.utils.Utils;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
        LoaderManager.LoaderCallbacks<Cursor>,
        ClusterManager.OnClusterItemClickListener<MapActivity.CustomClusterItem> {

    public static final int DEFAULT_ZOOM_LEVEL = 14;
    private static final String CAMERA_POSITION = "camera_position";
    private static final String MARKER_LAT_LONG = "marker_lat_long";
    private final String TAG = MapActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private List<BikePoint> mBikePoints = new ArrayList<>();
    private LatLng mLatLng;
    private BitmapDescriptor mMarkerIcon;
    private boolean mMarkersInMap = false;
    private ClusterManager<CustomClusterItem> mClusterManager;

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

        setupMarkerIcon();
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
        putMarkersInMap();
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
        return new BikePointProvider.AllBikePointCursorLoader(this);
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

    private void setupMarkerIcon() {
        mMarkerIcon = Utils.loadMarkerIcon(this);
    }

    private void handleNewLocation() {
        if (mCameraPosition == null) {
            mCameraPosition = new CameraPosition.Builder()
                    .target(mLatLng)
                    .zoom(DEFAULT_ZOOM_LEVEL)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        }
    }

    private void putMarkersInMap() {
        if (mMarkersInMap || mMap == null || mBikePoints == null || mBikePoints.size() < 1) {
            return;
        }

        mClusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);

        for (BikePoint bikepoint : mBikePoints) {
            CustomClusterItem clusterItem = new CustomClusterItem(bikepoint);
            mClusterManager.addItem(clusterItem);
        }

        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setRenderer(
                new CustomClusterRenderer(getApplicationContext(), mMap, mClusterManager));

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMarkersInMap = true;
    }

    @Override
    public boolean onClusterItemClick(CustomClusterItem customClusterItem) {
        return false;
    }

    public class CustomClusterItem implements ClusterItem {

        private final BikePoint mBikePoint;
        private final LatLng mPosition;

        public CustomClusterItem(BikePoint bikePoint) {
            mPosition = new LatLng(bikePoint.getLat(), bikePoint.getLon());
            mBikePoint = bikePoint;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        public BikePoint getBikePoint() {
            return mBikePoint;
        }
    }

    public class CustomClusterRenderer extends DefaultClusterRenderer<CustomClusterItem> {

        public CustomClusterRenderer(Context context, GoogleMap map,
                                     ClusterManager<CustomClusterItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterItemRendered(CustomClusterItem clusterItem, Marker marker) {
            marker.setIcon(mMarkerIcon);
            marker.setTitle(clusterItem.getBikePoint().getName());
            marker.setTag(clusterItem.getBikePoint().getId());
            super.onClusterItemRendered(clusterItem, marker);
        }
    }
}
