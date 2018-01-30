package com.pooja.anche.localads.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pooja.anche.localads.LocalAdDetailActivity;
import com.pooja.anche.localads.R;
import com.pooja.anche.localads.model.FirebaseConstants;
import com.pooja.anche.localads.model.LocalAd;
import com.pooja.anche.localads.viewholder.LocalAdViewHolder;

public abstract class LocalAdListFragment extends Fragment {

    private static final String TAG = "LocalAdListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<LocalAd, LocalAdViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public LocalAdListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_ads, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference(FirebaseConstants.FB_DB_LOCAL_AD_DB);
        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<LocalAd>()
                .setQuery(postsQuery, LocalAd.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<LocalAd, LocalAdViewHolder>(options) {

            @Override
            public LocalAdViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new LocalAdViewHolder(inflater.inflate(R.layout.item_ad, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(LocalAdViewHolder viewHolder, int position, final LocalAd model) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.bindToLocalAd(model);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch LocalAdDetailActivity
                        Intent intent = new Intent(getActivity(), LocalAdDetailActivity.class);
                        intent.putExtra(LocalAdDetailActivity.LOCAL_AD_KEY, postKey);
                        startActivity(intent);
                    }
                });

            }
        };
        mRecycler.setAdapter(mAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
