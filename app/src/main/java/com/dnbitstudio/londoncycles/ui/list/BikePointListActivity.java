package com.dnbitstudio.londoncycles.ui.list;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.BaseLocationActivity;
import com.dnbitstudio.londoncycles.ui.detail.BikePointDetailActivity;
import com.dnbitstudio.londoncycles.ui.detail.BikePointDetailFragment;
import com.dnbitstudio.londoncycles.ui.map.MapActivity;
import com.dnbitstudio.londoncycles.utils.Utils;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BikePointListActivity extends BaseLocationActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * List of Cursor columns to read from when preparing an adapter to populate the ListView.
     */
    private static final String[] FROM_COLUMNS = new String[]{
            BikePointProvider.COL_BIKE_POINT_ID,
            BikePointProvider.COL_BIKE_POINT_NAME
    };

    /**
     * List of Views which will be populated by Cursor data.
     */
    private static final int[] TO_FIELDS = new int[]{
            R.id.id,
            R.id.name};

    private static final String FAB_VISIBILITY = "fab_visibility";
    private final String TAG = BikePointListActivity.class.getSimpleName();
    @BindBool(R.bool.sw600)
    boolean mTwoPane;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bikepoint_list)
    ListView mListView;
    private int COLUMN_ID = 0;
    private SimpleCursorAdapter mAdapter;
    private BikePointDetailFragment mFragment;
    private boolean mIsFabVisible = false;
    private FloatingActionButton mFab;

    public static void launchActivity(Context context) {
        Intent intent = new Intent(context, BikePointListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikepoint_list);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mIsFabVisible = savedInstanceState.getBoolean(FAB_VISIBILITY);
        }

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        mAdapter = new SimpleCursorAdapter(
                this,       // Current context
                R.layout.bikepoint_list_content,  // Layout for individual rows
                null,                // Cursor
                FROM_COLUMNS,        // Cursor columns to use
                TO_FIELDS,           // Layout fields to use
                0                    // No flags
        );

        setupListView();
        setupFab();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FAB_VISIBILITY, mIsFabVisible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map) {
            MapActivity.launchActivity(this);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        return new BikePointProvider.AllBikePointCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished");
        mAdapter.changeCursor(cursor);
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
        mAdapter.changeCursor(null);
        mAdapter.swapCursor(null);
    }

    private void setupListView() {
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String bikePointId;
                if (cursor != null) {
                    bikePointId = cursor.getString(COLUMN_ID);
                } else {
                    bikePointId = ((TextView) view.findViewById(R.id.id)).getText().toString();
                }

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(BikePointDetailFragment.ARG_ITEM_ID, bikePointId);
                    mFragment = new BikePointDetailFragment();
                    mFragment.setArguments(arguments);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.bikepoint_detail_container, mFragment)
                            .commit();

                    mIsFabVisible = true;
                    if (mFab != null) {
                        mFab.setVisibility(View.VISIBLE);
                    }
                } else {
                    BikePointDetailActivity.launchActivity(getApplicationContext(), bikePointId);
                }
            }
        });
    }

    private void setupFab() {
        if (mTwoPane) {
            mFab = (FloatingActionButton) findViewById(R.id.fab);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double lat = mFragment.mBikePoint.getLat();
                    double lon = mFragment.mBikePoint.getLon();
                    startActivity(Utils.generateNavigationIntent(lat, lon));
                }
            });

            if (mIsFabVisible) {
                mFab.setVisibility(View.VISIBLE);
            }
        }
    }
}
