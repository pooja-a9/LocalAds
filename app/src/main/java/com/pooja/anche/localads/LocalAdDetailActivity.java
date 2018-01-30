package com.pooja.anche.localads;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pooja.anche.localads.adapter.ImagePagerAdapter;
import com.pooja.anche.localads.model.FirebaseConstants;
import com.pooja.anche.localads.model.LocalAd;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalAdDetailActivity extends BaseActivity {

    private static final String TAG = "LocalAdDetailActivity";

    public static final String LOCAL_AD_KEY = "local_ad_key";

    private DatabaseReference mLocalAdReference;
    private StorageReference mAdImageReference;
    private ValueEventListener mLocalAdListener;
    private String mAdKey;
    private ArrayList<Uri> mFileUris = null;

    @BindView(R.id.ad_v_author)
    TextView mAuthorView;

    @BindView(R.id.ad_v_title)
    TextView mTitleView;
    @BindView(R.id.ad_v_desc)
    TextView mDescView;

    @BindView(R.id.ad_v_phone)
    TextView mPhoneView;

    @BindView(R.id.ad_v_address)
    TextView mAddressView;

    @BindView(R.id.image_container)
    ViewPager mImageContainer;

    private ImagePagerAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localad_detail);

        // Get post key from intent
        mAdKey = getIntent().getStringExtra(LOCAL_AD_KEY);
        if (mAdKey == null) {
            throw new IllegalArgumentException("Must pass LOCAL_AD_KEY");
        }

        // Initialize Database
        mLocalAdReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.FB_DB_LOCAL_AD_DB).child(FirebaseConstants.FB_DB_LOCAL_AD).child(mAdKey);

        mAdImageReference = FirebaseStorage.getInstance().getReference().child(mAdKey);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

/*        mImageAdapter = new ImagePagerAdapter(this, getImageDownloadURLs());
        mImageContainer.setAdapter(mImageAdapter);*/

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener localAdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                LocalAd localAd = dataSnapshot.getValue(LocalAd.class);
                if (localAd != null) {
                    mAuthorView.setText(localAd.userName);
                    mTitleView.setText(localAd.title);
                    mDescView.setText(localAd.desc);

                    mPhoneView.setText(localAd.phone);
                    mAddressView.setText(localAd.address);

                    mImageAdapter = new ImagePagerAdapter(LocalAdDetailActivity.this, localAd.getLocalAdImageList());
                    mImageContainer.setAdapter(mImageAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(LocalAdDetailActivity.this, R.string.load_ad_failed, Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mLocalAdReference.addValueEventListener(localAdListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mLocalAdListener = localAdListener;
    }


    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mLocalAdListener != null) {
            mLocalAdReference.removeEventListener(mLocalAdListener);
        }
        // Clean up comments listener
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
