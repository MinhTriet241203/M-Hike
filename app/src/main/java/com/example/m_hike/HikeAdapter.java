package com.example.m_hike;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeViewHolder> {

    Context context;
    private List<Hike> hikeList = new ArrayList<>();

    public HikeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HikeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HikeViewHolder holder, int position) {
        //TODO: Set show image
        holder.nameText.setText(hikeList.get(position).getHikeName());
        holder.locationText.setText(hikeList.get(position).getLocation());
        holder.dateText.setText(hikeList.get(position).getDate());
        holder.itemLayout.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_diagonal));
    }

    @Override
    public int getItemCount() {
        return hikeList.size();
    }

    public void setHikeList(List<Hike> hikes) {
        this.hikeList = hikes;
        notifyDataSetChanged();
    }

    private Bitmap loadFromInternalStorage(String fileName) {
        File path = context.getFilesDir();
        try {
            File imageFile = new File(path, fileName);
            return BitmapFactory.decodeStream(new FileInputStream(imageFile));
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(context, "Cannot find image", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
