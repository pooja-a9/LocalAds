package com.pooja.anche.localads.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.pooja.anche.localads.R;

import java.util.ArrayList;

/**
 * Created by chaitanyay on 1/18/2018.
 */

public class ImagePagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;


    ArrayList<Uri> mResources;

    public ImagePagerAdapter(Context mContext, ArrayList<Uri> resources) {
        this.mContext = mContext;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResources = resources;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.image_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Glide.with(mContext).load(mResources.get(position)).into(imageView);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
