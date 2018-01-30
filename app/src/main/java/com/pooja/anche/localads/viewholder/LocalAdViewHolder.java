package com.pooja.anche.localads.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pooja.anche.localads.R;
import com.pooja.anche.localads.model.LocalAd;


public class LocalAdViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public TextView bodyView;

    public LocalAdViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.ad_v_title);
        authorView = itemView.findViewById(R.id.ad_v_author);
        bodyView = itemView.findViewById(R.id.ad_v_desc);
    }

    public void bindToLocalAd(LocalAd post) {
        titleView.setText(post.title);
        authorView.setText(post.userName);
        bodyView.setText(post.desc);

    }
}
