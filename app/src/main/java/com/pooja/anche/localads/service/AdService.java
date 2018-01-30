package com.pooja.anche.localads.service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pooja.anche.localads.R;
import com.pooja.anche.localads.SubmitAdActivity;
import com.pooja.anche.localads.model.FirebaseConstants;
import com.pooja.anche.localads.model.ImageInfo;
import com.pooja.anche.localads.model.LocalAd;
import com.pooja.anche.localads.widget.LocalAdWidget;

import java.util.ArrayList;

public class AdService extends JobService {

    DatabaseReference mLocalAdDbRef;

    public AdService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalAdDbRef = FirebaseDatabase.getInstance().getReference(FirebaseConstants.FB_DB_LOCAL_AD_DB);
    }


    @Override
    public boolean onStartJob(JobParameters job) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(getApplicationContext(), LocalAdWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }


}
