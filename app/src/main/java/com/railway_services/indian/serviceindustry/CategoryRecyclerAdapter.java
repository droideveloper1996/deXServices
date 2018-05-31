package com.railway_services.indian.serviceindustry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Abhishek on 08-03-2018.
 */

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder> {
    private Context mCtx;
    private int imageIds[];
    private String categoryName[];
    private CategoryItemClickListner categoryItemClickListner;

    public CategoryRecyclerAdapter(Context mCtx, int[] imageIds, String category[], CategoryItemClickListner categoryItemClickListner) {
        this.mCtx = mCtx;
        this.categoryItemClickListner = categoryItemClickListner;
        this.imageIds = imageIds;
        this.categoryName = category;
    }


    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.service_list_view, parent, false));

    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.list.setText(categoryName[position]);
        Picasso.with(mCtx).load(imageIds[position]).into(holder.imageView);
    }

    public interface CategoryItemClickListner {
        void onCategoryClick(int position);

    }

    @Override
    public int getItemCount() {
        return imageIds.length;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView list;
        ImageView imageView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            list = itemView.findViewById(R.id.categoryname);
            imageView = itemView.findViewById(R.id.cateoryImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            categoryItemClickListner.onCategoryClick(getAdapterPosition());
        }
    }
}
