package com.dnbitstudio.londoncycles.ui.detail;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.model.BikePoint;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;
import com.dnbitstudio.londoncycles.utils.CursorUtils;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
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
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private final String TAG = BikePointDetailFragment.class.getSimpleName();
    public BikePoint mBikePoint;
    @BindView(R.id.detail_name)
    TextView mName;
    @BindView(R.id.detail_bikes)
    TextView mBikes;
    @BindView(R.id.detail_empty)
    TextView mEmpty;
    private String mId;

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

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader");
        return new BikePointCursorLoader(getActivity(), mId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished");
        List<BikePoint> bikePoints = CursorUtils.parseBikePointsFromCursor(cursor);
        if (bikePoints != null && bikePoints.size() > 0) {
            mBikePoint = bikePoints.get(0);
            populateViews();
        }
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

    private static class BikePointCursorLoader extends CursorLoader {

        public BikePointCursorLoader(Context context, String id) {
            super(context, BikePointProvider.BIKE_POINTS, null,
                    BikePointProvider.COL_BIKE_POINT_ID + " = '" + id + "'", null, null);
        }
    }
}
