package com.example.m_hike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeViewHolder> {

    Context context;
    private List<Hike> hikeList = new ArrayList<>();
    private final SelectListener listener;

    public HikeAdapter(Context context, SelectListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HikeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HikeViewHolder holder, int position) {
        holder.nameText.setText(hikeList.get(position).getHikeName());
        holder.locationText.setText(hikeList.get(position).getLocation());
        holder.dateText.setText(hikeList.get(position).getDate());
        holder.lengthText.setText(String.valueOf(hikeList.get(position).getLength()));
        holder.itemLayout.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_diagonal));
        holder.itemLayout.setOnClickListener(v -> listener.onItemClicked(hikeList.get(position)));
    }

    @Override
    public int getItemCount() {
        return hikeList.size();
    }

    public void setHikeList(List<Hike> hike) {
        this.hikeList = hike;
        notifyDataSetChanged();
    }
}
