package com.railway_services.indian.serviceindustry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appnext.base.Appnext;
import com.appnext.core.Ad;
import com.appnext.core.AppnextError;
import com.appnext.nativeads.MediaView;
import com.appnext.nativeads.NativeAd;
import com.appnext.nativeads.NativeAdListener;
import com.appnext.nativeads.NativeAdRequest;
import com.appnext.nativeads.NativeAdView;
import com.appnext.nativeads.PrivacyIcon;

import java.util.ArrayList;

/**
 * Created by Abhishek on 25-03-2018.
 */

public class AdsRecyclerAdapter extends RecyclerView.Adapter<AdsRecyclerAdapter.AdsViewHolder> {

    Context mCtx;
    NativeAd nativeAd;

    AdsRecyclerAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public AdsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AdsViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.app_next_native_ads, parent, false));
    }

    @Override
    public void onBindViewHolder(final AdsViewHolder holder, int position) {

        Appnext.init(mCtx);

        nativeAd = new NativeAd(mCtx, ConstantUtils.APPNEXT_PLACEMENT_ID);
        nativeAd.setPrivacyPolicyColor(PrivacyIcon.PP_ICON_COLOR_DARK);
        nativeAd.setPrivacyPolicyPosition(PrivacyIcon.PP_ICON_POSITION_BOTTOM_LEFT);
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(final NativeAd nativeAd) {
                super.onAdLoaded(nativeAd);
                holder.progressBar.setVisibility(View.GONE);
                nativeAd.downloadAndDisplayImage(holder.imageView, nativeAd.getIconURL());
                holder.textView.setText(nativeAd.getAdTitle());
                nativeAd.setMediaView(holder.mediaView);
                holder.rating.setText(nativeAd.getStoreRating());
                holder.description.setText(nativeAd.getAdDescription());
                nativeAd.registerClickableViews(holder.viewArrayList);
                nativeAd.setNativeAdView(holder.nativeAdView);
            }

            @Override
            public void onAdClicked(NativeAd nativeAd) {
                super.onAdClicked(nativeAd);
            }

            @Override
            public void onError(NativeAd nativeAd, AppnextError appnextError) {
                super.onError(nativeAd, appnextError);
                Toast.makeText(mCtx, "Error loading ads", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void adImpression(NativeAd nativeAd) {
                super.adImpression(nativeAd);
            }
        });


        nativeAd.loadAd(new NativeAdRequest()
                // optional - config your ad request:
                .setPostback("")
                .setCategories("")
                .setCachingPolicy(NativeAdRequest.CachingPolicy.ALL)
                .setCreativeType(NativeAdRequest.CreativeType.ALL)
                .setVideoLength(NativeAdRequest.VideoLength.SHORT)
                .setVideoQuality(NativeAdRequest.VideoQuality.HIGH)
        );


    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class AdsViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        MediaView mediaView;
        ProgressBar progressBar;
        Button button;
        NativeAd nativeAd;
        TextView rating;
        TextView description;
        NativeAdView nativeAdView;
        ArrayList<View> viewArrayList;

        public AdsViewHolder(View itemView) {
            super(itemView);
            nativeAdView = (NativeAdView) itemView.findViewById(R.id.na_view);
            imageView = (ImageView) itemView.findViewById(R.id.na_icon);
            textView = (TextView) itemView.findViewById(R.id.na_title);
            mediaView = (MediaView) itemView.findViewById(R.id.na_media);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            button = (Button) itemView.findViewById(R.id.install);
            rating = (TextView) itemView.findViewById(R.id.rating);
            description = (TextView) itemView.findViewById(R.id.description);
            viewArrayList = new ArrayList<>();
            viewArrayList.add(button);
            viewArrayList.add(mediaView);
        }
    }
}
