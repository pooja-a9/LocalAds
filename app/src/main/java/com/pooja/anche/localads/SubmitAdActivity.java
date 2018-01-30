package com.pooja.anche.localads;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pooja.anche.localads.adapter.ImagePagerAdapter;
import com.pooja.anche.localads.model.FirebaseConstants;
import com.pooja.anche.localads.model.ImageInfo;
import com.pooja.anche.localads.model.LocalAd;
import com.pooja.anche.localads.service.UploadService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubmitAdActivity extends AppCompatActivity {

    @BindView(R.id.adType_spinner)
    Spinner mSpinner;

    @BindView(R.id.ad_title)
    TextView mAdTitle;

    @BindView(R.id.ad_title_desc)
    TextView mAdDesc;
    @BindView(R.id.ad_phone)
    TextView mAdPhone;
    @BindView(R.id.ad_email)
    TextView mAdEmail;
    @BindView(R.id.ad_address)
    TextView mAdAddress;

    @BindView(R.id.ad_address_city)
    TextView mAdCity;
    @BindView(R.id.ad_address_zip)
    TextView mAdZip;
    @BindView(R.id.image_count)
    TextView mImageCount;
    @BindView(R.id.adType_state_spinner)
    Spinner mStateSpinner;

    @BindView(R.id.image_container)
    ViewPager mImageContainer;

    FirebaseUser mUser;
    DatabaseReference mLocalAdDbRef;
    StorageReference mStorageRef;

    private ArrayList<Uri> mFileUris = null;

    private static final String REQUIRED = "Required";

    public static final String UPLOAD_FILES = "UPLOAD_FILES";
    public static final String SUBMIT_AD_KEY = "SUBMIT_AD_KEY";

    private static final int RC_TAKE_PICTURE = 101;
    private static final String TAG = "SubmitAdActivity";

    private ImagePagerAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_ad);
        ButterKnife.bind(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mLocalAdDbRef = database.getReference(FirebaseConstants.FB_DB_LOCAL_AD_DB);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFileUris = new ArrayList<>();

    }


    public void submitAd(View view) {

        final String title = mAdTitle.getText().toString();
        final String desc = mAdDesc.getText().toString();
        final String phone = mAdPhone.getText().toString();
        final String address = mAdAddress.getText().toString();
        final String city = mAdCity.getText().toString();
        final String zip = mAdZip.getText().toString();
        final String email = mAdEmail.getText().toString();

        if (TextUtils.isEmpty(title)) {
            mAdTitle.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(desc)) {
            mAdDesc.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            mAdPhone.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(address)) {
            mAdAddress.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(city)) {
            mAdCity.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(zip)) {
            mAdZip.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(email)) {
            mAdEmail.setError(REQUIRED);
            return;
        }

        StringBuffer sbAddress = new StringBuffer();
        sbAddress.append(address).append(",")
                .append(city).append(",")
                .append(mStateSpinner.getSelectedItem().toString())
                .append(",").append(zip);

        LocalAd localAd = new LocalAd(title,
                desc,
                phone,
                sbAddress.toString(),
                email,
                mSpinner.getSelectedItem().toString(),
                mUser.getDisplayName(),
                mUser.getUid());

        String key = mLocalAdDbRef.child(FirebaseConstants.FB_DB_LOCAL_AD).push().getKey();

        mLocalAdDbRef.child(FirebaseConstants.FB_DB_LOCAL_AD).child(key).setValue(localAd);

        uploadFromUri(mFileUris, key);

        Intent navigateToMain = new Intent(getApplicationContext(), LocalAdsActivity.class);

        navigateToMain.putExtra(LocalAdsActivity.AD_POSTED, true);
        startActivity(navigateToMain);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void uploadImages(View view) {

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mFileUris = new ArrayList<>();
                ClipData clipData = data.getClipData();

                if (clipData != null) {

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        mFileUris.add(clipData.getItemAt(i).getUri());
                    }
                } else {
                    mFileUris.add(data.getData());
                }
                if (!mFileUris.isEmpty()) {
                    Log.d(TAG, "File URI's are" + mFileUris);

                    mImageAdapter = new ImagePagerAdapter(this, mFileUris);
                    mImageContainer.setAdapter(mImageAdapter);
                    mImageCount.setText(String.format(getString(R.string.submit_image_count), mFileUris.size()));

                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, R.string.select_pic_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFromUri(ArrayList<Uri> fileUri, final String key) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putParcelableArrayList(UPLOAD_FILES, fileUri);
        myExtrasBundle.putString(SUBMIT_AD_KEY, key);

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(UploadService.class)
                // uniquely identifies the job
                .setTag("my-unique-tag")
                // one-off job
                .setRecurring(false)
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_UNMETERED_NETWORK,
                        // only run when the device is charging
                        Constraint.DEVICE_CHARGING
                )
                .setExtras(myExtrasBundle).build();
        dispatcher.mustSchedule(myJob);

    }
}
