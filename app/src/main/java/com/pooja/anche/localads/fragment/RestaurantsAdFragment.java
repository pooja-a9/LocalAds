package com.pooja.anche.localads.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pooja.anche.localads.model.FirebaseConstants;

public class RestaurantsAdFragment extends LocalAdListFragment {

    public RestaurantsAdFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child(FirebaseConstants.FB_DB_LOCAL_AD).orderByChild("type").equalTo("Restaurants").limitToFirst(100);
    }
}
