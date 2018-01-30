package com.pooja.anche.localads.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pooja.anche.localads.model.FirebaseConstants;

public class CarAdsFragment extends LocalAdListFragment {

    public CarAdsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]
        // My top posts by number of stars
        String myUserId = getUid();
        return databaseReference.child(FirebaseConstants.FB_DB_LOCAL_AD).orderByChild("type").equalTo("Cars").limitToFirst(100);
        // [END my_top_posts_query]
    }
}
