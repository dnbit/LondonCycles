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

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BikePointListActivity extends AppCompatActivity {

    @BindBool(R.bool.sw600)
    boolean mTwoPane;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bikepoint_list)
    RecyclerView mRecyclerView;

    public static void launchActivity(Context context) {
        Intent intent = new Intent(context, BikePointListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikepoint_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        if (mRecyclerView != null) {
            setupRecyclerView(mRecyclerView);
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
            holder.mContentView.setText(mBikePoints.get(position).getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bikePointId = holder.mItem.getId();
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(BikePointDetailFragment.ARG_ITEM_ID, bikePointId);
                        BikePointDetailFragment fragment = new BikePointDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.bikepoint_detail_container, fragment)
                                .commit();
                    } else {
                        BikePointDetailActivity.launchActivity(v.getContext(), bikePointId);
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
            public BikePoint mItem;

            @BindView(R.id.id)
            public TextView mIdView;
            @BindView(R.id.content)
            public TextView mContentView;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mView = view;
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
