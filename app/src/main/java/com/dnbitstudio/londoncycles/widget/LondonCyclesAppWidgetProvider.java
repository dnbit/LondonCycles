package com.dnbitstudio.londoncycles.widget;

import com.dnbitstudio.londoncycles.sync.SyncAdapter;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class LondonCyclesAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (SyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            LondonCyclesAppWidgetIntentService.launchService(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        LondonCyclesAppWidgetIntentService.launchService(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

