package com.pooja.anche.localads.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pooja.anche.localads.R;
import com.pooja.anche.localads.model.FirebaseConstants;
import com.pooja.anche.localads.model.LocalAd;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sweetie on 10/16/2017.
 */

public class LocalAdWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new LocalAdListProvider(this.getApplicationContext(), intent));
    }

    class LocalAdListProvider implements RemoteViewsService.RemoteViewsFactory {

        private Context context = null;
        DatabaseReference mLocalAdReference;
        List<LocalAd> adsList;


        public LocalAdListProvider(Context applicationContext, Intent intent) {
            this.context = applicationContext;
        }


        @Override
        public void onCreate() {
            adsList = new ArrayList<>();
            Query localAdQuery = FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConstants.FB_DB_LOCAL_AD_DB).child(FirebaseConstants.FB_DB_LOCAL_AD).limitToLast(5);

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        LocalAd localAd = ds.getValue(LocalAd.class);
                        adsList.add(localAd);
                    }
                   AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                            new ComponentName(context, LocalAdWidget.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            localAdQuery.addListenerForSingleValueEvent(eventListener);
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return adsList.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            final RemoteViews remoteView = new RemoteViews(
                    context.getPackageName(), R.layout.local_ad_widget_list);
            LocalAd localAd = adsList.get(i);
            remoteView.setTextViewText(R.id.ad_v_title, localAd.getTitle());
            remoteView.setTextViewText(R.id.ad_v_desc, localAd.getDesc());

            // Intent intent = new Intent();
            // remoteView.setOnClickFillInIntent(R.id.recipe_ingredient, intent);

            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
