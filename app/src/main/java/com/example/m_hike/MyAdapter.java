package com.example.m_hike;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<Hike> hikeList;

    public MyAdapter(Context context, List<Hike> hikeList) {
        this.context = context;
        this.hikeList = hikeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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
