package com.pooja.anche.localads;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pooja.anche.localads.fragment.GroupAdFragment;
import com.pooja.anche.localads.fragment.RentalAdFragment;
import com.pooja.anche.localads.fragment.RestaurantsAdFragment;
import com.pooja.anche.localads.fragment.CarAdsFragment;
import com.pooja.anche.localads.fragment.EducationAdFragment;
import com.pooja.anche.localads.fragment.SportsAdFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalAdsActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    private FragmentPagerAdapter mPagerAdapter;

    public static final String AD_POSTED = "AD_POSTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_ads);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        View headerLayout = mNavigationView.getHeaderView(0);

        TextView userName = headerLayout.findViewById(R.id.user_name);
        TextView userEmail = headerLayout.findViewById(R.id.user_email);
        ImageView userimage = headerLayout.findViewById(R.id.imageView);


        userName.setText(mUser.getDisplayName());
        userEmail.setText(mUser.getEmail());

        Glide.with(mNavigationView).load(mUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(userimage);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SubmitAdActivity.class));
            }
        });

        if (getIntent().getBooleanExtra(AD_POSTED, false)) {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.drawer_layout),
                    "Local Ad Posted", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);

        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        //Ad List Fragments
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new RentalAdFragment(),
                    new CarAdsFragment(),
                    new GroupAdFragment(),
                    new EducationAdFragment(),
                    new RestaurantsAdFragment(),
                    new SportsAdFragment(),

            };
            private final String[] mFragmentNames = new String[]{
                    getString(R.string.rentals),
                    getString(R.string.cars),
                    getString(R.string.group_activities),
                    getString(R.string.education),
                    getString(R.string.restaurants),
                    getString(R.string.sports_events),

            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.local_ads, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rental) {
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.nav_cars) {
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.nav_group) {
            mViewPager.setCurrentItem(2);
        } else if (id == R.id.nav_education) {
            mViewPager.setCurrentItem(3);
        } else if (id == R.id.nav_restaurants) {
            mViewPager.setCurrentItem(4);
        } else if (id == R.id.nav_sports) {
            mViewPager.setCurrentItem(5);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
