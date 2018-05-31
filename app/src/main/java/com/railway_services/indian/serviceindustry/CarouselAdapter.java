package com.railway_services.indian.serviceindustry;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.security.spec.ECField;
import java.util.ArrayList;

/**
 * Created by Abhishek on 09-03-2018.
 */

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {


    public ServiceOnClickInterface serviceOnClickInterface;
    private Context mCtx;
    private int mServiceType;
    private ArrayList<ServiceClass> serviceClassArrayList;

    public CarouselAdapter(Context mCtx, ArrayList<ServiceClass> serviceClassArrayList, ServiceOnClickInterface serviceOnClickInterface, int mServiceType) {
        this.mCtx = mCtx;
        this.mServiceType = mServiceType;
        this.serviceClassArrayList = serviceClassArrayList;
        this.serviceOnClickInterface = serviceOnClickInterface;
    }

    @Override
    public CarouselViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CarouselViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.carousel_list, parent, false));
    }

    @Override
    public void onBindViewHolder(CarouselViewHolder holder, int position) {
        try {
            Picasso.with(mCtx).load(serviceClassArrayList.get(position).getmServiceThumbnailImage()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);
            holder.textView.setText(serviceClassArrayList.get(position).getmServiceName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface ServiceOnClickInterface {
        void click(int position, int params);

    }

    @Override
    public int getItemCount() {
        return serviceClassArrayList.size();
    }

    public class CarouselViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;

        public CarouselViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carouselImageView);
            textView = itemView.findViewById(R.id.subscriptionserviceName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            serviceOnClickInterface.click(position, mServiceType);
        }
    }
}
