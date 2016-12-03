package com.dnbitstudio.londoncycles.ui;

import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BikePointListActivity.launchActivity(this);

        finish();
    }
}
