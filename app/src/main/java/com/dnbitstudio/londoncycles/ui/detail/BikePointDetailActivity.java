package com.dnbitstudio.londoncycles.ui.detail;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;
import com.dnbitstudio.londoncycles.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity representing a single BikePoint detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link BikePointListActivity}.
 */
public class BikePointDetailActivity extends AppCompatActivity {

    private final String TAG = BikePointDetailActivity.class.getSimpleName();

    @BindBool(R.bool.rtl)
    boolean mRtl;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    private BikePointDetailFragment mFragment;

    public static void launchActivity(Context context, String bikePointId) {
        Intent intent = new Intent(context, BikePointDetailActivity.class);
        intent.putExtra(BikePointDetailFragment.ARG_ITEM_ID, bikePointId);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikepoint_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (mRtl) {
            mCollapsingToolbarLayout.setExpandedTitleGravity(Gravity.END | Gravity.BOTTOM);
            mCollapsingToolbarLayout
                    .setCollapsedTitleGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            String bikePointId = getIntent().getStringExtra(BikePointDetailFragment.ARG_ITEM_ID);
            arguments.putString(BikePointDetailFragment.ARG_ITEM_ID, bikePointId);
            mFragment = new BikePointDetailFragment();
            mFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.bikepoint_detail_container, mFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, BikePointListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void fabClicked() {
        double lat = mFragment.mBikePoint.getLat();
        double lon = mFragment.mBikePoint.getLon();
        startActivity(Utils.generateNavigationIntent(lat, lon));
    }
}
