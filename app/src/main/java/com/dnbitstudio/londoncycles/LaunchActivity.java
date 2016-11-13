package com.dnbitstudio.londoncycles;

import com.dnbitstudio.londoncycles.ui.MapActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapActivity.launchActivity(this);
        finish();
    }
}
