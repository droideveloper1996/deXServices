package com.railway_services.indian.serviceindustry;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abhishek on 09-03-2018.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context mCtx;
    LayoutInflater layoutInflater;
    ArrayList<String> SliderData;
    String carouselImage[] = {"https://bbmlive.com/wp-content/uploads/2015/05/Carpenter.jpg"
            , "https://cdn.websites.hibu.com/2c80e0994f7f483dbc374e2ad384fe51/dms3rep/multi/tablet/plumbing2-780x350.jpg",
            "https://bbmlive.com/wp-content/uploads/2015/05/Carpenter.jpg"
            , "https://cdn.websites.hibu.com/2c80e0994f7f483dbc374e2ad384fe51/dms3rep/multi/tablet/plumbing2-780x350.jpg", "https://bbmlive.com/wp-content/uploads/2015/05/Carpenter.jpg"
            , "https://cdn.websites.hibu.com/2c80e0994f7f483dbc374e2ad384fe51/dms3rep/multi/tablet/plumbing2-780x350.jpg"};

    public ViewPagerAdapter(Context mCtx, ArrayList<String> data) {
        this.mCtx = mCtx;
        this.SliderData=data;
    }

    @Override
    public int getCount() {
        return SliderData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = view.findViewById(R.id.carouselImages);
        Picasso.with(mCtx).load(SliderData.get(position)).into(imageView);
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
