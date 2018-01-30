package com.pooja.anche.localads.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pooja.anche.localads.model.FirebaseConstants;

public class EducationAdFragment extends LocalAdListFragment {

    public EducationAdFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        return databaseReference.child(FirebaseConstants.FB_DB_LOCAL_AD).orderByChild("type").equalTo("Education").limitToFirst(100);
        // [END recent_posts_query]


    }
}
