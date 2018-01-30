package com.pooja.anche.localads.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pooja.anche.localads.SubmitAdActivity;
import com.pooja.anche.localads.model.FirebaseConstants;
import com.pooja.anche.localads.model.ImageInfo;

import java.util.ArrayList;

public class UploadService extends JobService {


    private StorageReference mStorageRef;
    private static final String TAG = "MyUploadService";
    DatabaseReference mLocalAdDbRef;

    public UploadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mLocalAdDbRef = FirebaseDatabase.getInstance().getReference(FirebaseConstants.FB_DB_LOCAL_AD_DB);
    }


    @Override
    public boolean onStartJob(JobParameters job) {

        ArrayList<Uri> fileUri;
        final String key;

        if (job.getExtras().containsKey(SubmitAdActivity.UPLOAD_FILES) && job.getExtras().containsKey(SubmitAdActivity.SUBMIT_AD_KEY)) {
            fileUri = job.getExtras().getParcelableArrayList(SubmitAdActivity.UPLOAD_FILES);
            key = job.getExtras().getString(SubmitAdActivity.SUBMIT_AD_KEY);


            int count = 0;
            for (Uri uri : fileUri) {


                final StorageReference photoRef = mStorageRef.child(key)
                        .child(String.valueOf(count));

                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .setCustomMetadata("LoaclAdId", key)
                        .build();
                // [END get_child_ref]

                // Upload file to Firebase Storage
                Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
                photoRef.putFile(uri, metadata)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Upload succeeded
                                Log.d(TAG, "uploadFromUri:onSuccess");
                                // Get the public download URL
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                ImageInfo image = new ImageInfo(downloadUri.toString());

                                DatabaseReference adRefrence = mLocalAdDbRef.child(FirebaseConstants.FB_DB_LOCAL_AD)
                                        .child(key).child(FirebaseConstants.FB_DB_LOCAL_AD_IMAGES);
                                String userId = adRefrence.push().getKey();
                                adRefrence.child(userId).setValue(image);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Upload failed
                                Log.w(TAG, "uploadFromUri:onFailure", exception);
                            }
                        });
                count++;
            }
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }


}
