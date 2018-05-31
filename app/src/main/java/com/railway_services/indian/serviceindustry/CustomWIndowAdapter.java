package com.railway_services.indian.serviceindustry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abhishek on 05-04-2018.
 */

public class CustomWIndowAdapter implements GoogleMap.InfoWindowAdapter {


    private Context mCtx;
    private View windowView;
    private ArrayList<String> url;

    CustomWIndowAdapter(Context mCtx,ArrayList<String> url) {
        this.mCtx = mCtx;
        this.url=url;
        windowView = LayoutInflater.from(mCtx).inflate(R.layout.custom_detail_view, null);
    }

    void renderView(Marker marker) {
        ImageView imageView = windowView.findViewById(R.id.imageViewProfile);
        TextView name = windowView.findViewById(R.id.name);
        TextView mobile = windowView.findViewById(R.id.mobile);
        TextView status = windowView.findViewById(R.id.status);

        name.setText(marker.getTitle());
        mobile.setText(marker.getSnippet());

        Picasso.with(mCtx).load(marker.getSnippet()).into(imageView);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderView(marker);
        return windowView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderView(marker);
        return windowView;
    }
}
