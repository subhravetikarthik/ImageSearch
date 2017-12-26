package com.demoapp.fieldwire;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private List<Photo> photos;
    private Context appContext;
    private CustomItemClickListener mListener;

    public DataAdapter(List<Photo> photos, Context context) {
        this.photos = photos;
        this.appContext = context;

        Log.d("Karthik", "DataAdapter photos: " + photos.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Karthik", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_row, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);
        view.setOnClickListener(v -> mListener.onItemClick(v, mViewHolder.getAdapterPosition()));
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        Log.d("Karthik", "onBindViewHolder");
        holder.listingTitle.setText(photos.get(position).getTitle());
        String url = photos.get(position).getUrl();
        Log.d("Karthik", "Title: " + photos.get(position).getTitle());
        Log.d("Karthik", "url: " + url);
        Picasso.with(appContext)
                .load(url)
                .into(holder.listingImage);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setClickListener(CustomItemClickListener itemClickListener) {
        this.mListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView listingImage;
        private TextView listingTitle;

        public ViewHolder(View view) {
            super(view);
            listingImage = view.findViewById(R.id.imageView);
            listingTitle = view.findViewById(R.id.title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onItemClick(v, getAdapterPosition());
        }
    }
}
