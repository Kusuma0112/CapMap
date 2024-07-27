package com.example.capmap.fragements.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capmap.R;

import java.util.List;

public class EventPagerAdapter extends RecyclerView.Adapter<EventPagerAdapter.EventViewHolder> {

    private Context context;
    private List<Integer> eventImages;

    public EventPagerAdapter(Context context, List<Integer> eventImages) {
        this.context = context;
        this.eventImages = eventImages;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Glide.with(context).load(eventImages.get(position)).into(holder.eventImage);
    }

    @Override
    public int getItemCount() {
        return eventImages.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
        }
    }
}
