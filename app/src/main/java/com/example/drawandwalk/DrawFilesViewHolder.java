package com.example.drawandwalk;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DrawFilesViewHolder extends RecyclerView.ViewHolder{
    TextView tvFile;

    public DrawFilesViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        tvFile=itemView.findViewById(R.id.tvFile);
    }
}
