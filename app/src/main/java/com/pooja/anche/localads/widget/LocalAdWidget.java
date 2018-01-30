package com.pooja.anche.localads.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.pooja.anche.localads.LocalAdsActivity;
import com.pooja.anche.localads.R;
import com.pooja.anche.localads.service.AdService;

/**
 * Implementation of App Widget functionality.
 */
public class LocalAdWidget extends AppWidgetProvider {

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.local_ad_widget);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, LocalAdWidgetService.class);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(R.id.widget_listview, svcIntent);
        //setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.widget_listview, R.id.empty_view);

        Intent intent = new Intent(context, LocalAdsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);


        return remoteViews;
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            appWidgetManager.updateAppWidget(appWidgetId, updateWidgetListView(context, appWidgetId));
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString("some_key", "some_value");
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(AdService.class)
                // uniquely identifies the job
                .setTag("My-AdWidget-service")
                // one-off job
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(0, 1))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_UNMETERED_NETWORK,
                        // only run when the device is charging
                        Constraint.DEVICE_CHARGING
                )
                .setExtras(myExtrasBundle)
                .build();

        dispatcher.mustSchedule(myJob);

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

