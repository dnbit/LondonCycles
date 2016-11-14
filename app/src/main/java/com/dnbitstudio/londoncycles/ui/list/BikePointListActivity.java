package com.dnbitstudio.londoncycles.ui.list;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.dnbitstudio.londoncycles.BuildConfig;
import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.model.BikePoint;
import com.dnbitstudio.londoncycles.ui.map.MapActivity;
import com.dnbitstudio.londoncycles.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BikePointListActivity extends AppCompatActivity {

    private boolean mTwoPane;

    public static void launchActivity(Context context) {
        Intent intent = new Intent(context, BikePointListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikepoint_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        View recyclerView = findViewById(R.id.bikepoint_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.bikepoint_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if (BuildConfig.DEBUG) {
            String mockedJson = Utils.loadMockedBikePoints(this);
            List<BikePoint> bikePoints
                    = new Gson().fromJson(mockedJson, new TypeToken<List<BikePoint>>() {
            }.getType());
            recyclerView.setAdapter(new BikePointListAdapter(bikePoints));
        }
    }

    public class BikePointListAdapter
            extends RecyclerView.Adapter<BikePointListAdapter.ViewHolder> {

        private final List<BikePoint> mBikePoints;

        public BikePointListAdapter(List<BikePoint> bikePoints) {
            mBikePoints = bikePoints;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bikepoint_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mBikePoints.get(position);
            holder.mIdView.setText(mBikePoints.get(position).getId());
            holder.mContentView.setText(mBikePoints.get(position).getCommonName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(BikePointDetailFragment.ARG_ITEM_ID,
                                holder.mItem.getId());
                        BikePointDetailFragment fragment = new BikePointDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.bikepoint_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, BikePointDetailActivity.class);
                        intent.putExtra(BikePointDetailFragment.ARG_ITEM_ID, holder.mItem.getId());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mBikePoints.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public BikePoint mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
