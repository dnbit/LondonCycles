package com.dnbitstudio.londoncycles.ui.detail;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.model.BikePoint;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;
import com.dnbitstudio.londoncycles.utils.CursorUtils;
import com.dnbitstudio.londoncycles.utils.Utils;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single BikePoint detail screen.
 * This fragment is either contained in a {@link BikePointListActivity}
 * in two-pane mode (on tablets) or a {@link BikePointDetailActivity}
 * on handsets.
 */
public class BikePointDetailFragment extends Fragment
        implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final int DEFAULT_ZOOM_LEVEL = 15;
    private final String TAG = BikePointDetailFragment.class.getSimpleName();
    public BikePoint mBikePoint;
    @BindView(R.id.detail_name)
    TextView mName;
    @BindView(R.id.detail_bikes)
    TextView mBikes;
    @BindView(R.id.detail_empty)
    TextView mEmpty;
    @BindView(R.id.map)
    MapView mMapView;
    private GoogleMap mMap;
    private String mId;
    private CameraPosition mCameraPosition;
    private BitmapDescriptor mMarkerIcon;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BikePointDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mId = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bikepoint_detail, container, false);
        ButterKnife.bind(this, rootView);

        getLoaderManager().initLoader(0, null, this);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        setupMarkerIcon();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        MapsInitializer.initialize(this.getActivity());

        initCameraPosition();
    }

    private void initCameraPosition() {
        if (mBikePoint != null && mMap != null && mCameraPosition == null) {
            LatLng latLng = new LatLng(mBikePoint.getLat(), mBikePoint.getLon());
            mCameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(DEFAULT_ZOOM_LEVEL)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

            mMap.clear();
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            markerOptions.icon(mMarkerIcon);
            mMap.addMarker(markerOptions);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader");
        return new BikePointProvider.IdBikePointCursorLoader(getActivity(), mId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished");
        List<BikePoint> bikePoints = CursorUtils.parseBikePointsFromCursor(cursor);
        if (bikePoints != null && bikePoints.size() > 0) {
            mBikePoint = bikePoints.get(0);
            populateViews();
        }
        initCameraPosition();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    private void populateViews() {
        if (mBikePoint != null) {
            mName.setText(mBikePoint.getName());
            mBikes.setText(String.valueOf(mBikePoint.getBikes()));
            mEmpty.setText(String.valueOf(mBikePoint.getEmpty()));
        }
    }

    private void setupMarkerIcon() {
        mMarkerIcon = Utils.loadMarkerIcon(getActivity().getApplicationContext());
    }
}
