package com.example.m_hike;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HikeViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameText, locationText, dateText;
    RelativeLayout itemLayout;

    public HikeViewHolder(@NonNull View itemView) {
        super(itemView);

        itemLayout = itemView.findViewById(R.id.itemLayout);
        imageView = itemView.findViewById(R.id.imageView);
        nameText = itemView.findViewById(R.id.nameText);
        locationText = itemView.findViewById(R.id.locationText);
        dateText = itemView.findViewById(R.id.dateText);
    }
}
