package com.dnbitstudio.londoncycles.widget;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.provider.BikePointProvider;
import com.dnbitstudio.londoncycles.ui.detail.BikePointDetailActivity;
import com.dnbitstudio.londoncycles.ui.detail.BikePointDetailFragment;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.RemoteViews;

public class LondonCyclesAppWidgetIntentService extends IntentService
        implements Loader.OnLoadCompleteListener<Cursor> {

    public static final int CURSOR_LOADER_LISTENER_ID = 1;
    public static final int WAIT_MILLIS = 1000;
    private final String TAG = LondonCyclesAppWidgetIntentService.class.getSimpleName();
    private BikePointProvider.ClosestBikePointCursorLoader mCursorLoader;
    private boolean mWaiting;

    public LondonCyclesAppWidgetIntentService() {
        super("LondonCyclesAppWidgetIntentService");
    }

    public static void launchService(Context context) {
        Intent intent = new Intent(context, LondonCyclesAppWidgetIntentService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        mCursorLoader = new BikePointProvider.ClosestBikePointCursorLoader(this);
        mCursorLoader.registerListener(CURSOR_LOADER_LISTENER_ID, this);
        mCursorLoader.startLoading();
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        mWaiting = true;
        while (mWaiting) {
            try {
                Thread.currentThread().sleep(WAIT_MILLIS);
            } catch (InterruptedException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        // Stop the cursor loader
        if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
        super.onDestroy();
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadComplete");

        if (!cursor.moveToFirst()) {
            Log.d(TAG, "Table is empty");
        }

        if (cursor.moveToFirst()) {
            String id = cursor.getString(
                    cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_ID));
            Log.d(TAG, "on cursor with id: " + id);
            String name = cursor.getString(
                    cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_NAME));
            /*double lat = cursor.getDouble(
                    cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_LAT));
            double lon = cursor.getDouble(
                    cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_LON));
            int docks = cursor.getInt(
                    cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_DOCKS));*/
            int empty = cursor.getInt(
                    cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_EMPTY));
            int bikes = cursor.getInt(
                    cursor.getColumnIndex(BikePointProvider.COL_BIKE_POINT_BIKES));

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                    LondonCyclesAppWidgetProvider.class));

            for (int appWidgetId : appWidgetIds) {
                // Construct the RemoteViews object
                RemoteViews remoteViews = new RemoteViews(
                        getPackageName(), R.layout.london_cycles_app_widget_provider);
                remoteViews.setTextViewText(R.id.appwidget_name, name);
                remoteViews.setTextViewText(R.id.appwidget_bikes, String.valueOf(bikes));
                remoteViews.setTextViewText(R.id.appwidget_empty, String.valueOf(empty));

                // Create an Intent to launch MainActivity
                Intent launchIntent = new Intent(this, BikePointDetailActivity.class);
                launchIntent.putExtra(BikePointDetailFragment.ARG_ITEM_ID, id);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }

        mWaiting = false;
    }
}
