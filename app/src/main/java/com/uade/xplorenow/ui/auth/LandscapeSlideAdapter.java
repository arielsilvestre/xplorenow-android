package com.uade.xplorenow.ui.auth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uade.xplorenow.R;

public class LandscapeSlideAdapter extends RecyclerView.Adapter<LandscapeSlideAdapter.SlideViewHolder> {

    private static final int[] LANDSCAPES = {
            R.drawable.bg_landscape_ocean,
            R.drawable.bg_landscape_forest,
            R.drawable.bg_landscape_mountain,
            R.drawable.bg_landscape_aurora,
            R.drawable.bg_landscape_caribbean
    };

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_landscape_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.itemView.setBackgroundResource(LANDSCAPES[position]);
    }

    @Override
    public int getItemCount() {
        return LANDSCAPES.length;
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
