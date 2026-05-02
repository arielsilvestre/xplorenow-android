package com.uade.xplorenow.ui.activities;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.databinding.ItemActivityCompactBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityCompactAdapter extends RecyclerView.Adapter<ActivityCompactAdapter.ViewHolder> {

    public interface OnActivityClickListener {
        void onActivityClick(TourActivity activity);
    }

    private List<TourActivity> activities = new ArrayList<>();
    private final OnActivityClickListener listener;

    public ActivityCompactAdapter(OnActivityClickListener listener) {
        this.listener = listener;
    }

    public void setActivities(List<TourActivity> list) {
        activities = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActivityCompactBinding binding = ItemActivityCompactBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(activities.get(position));
    }

    @Override
    public int getItemCount() { return activities.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemActivityCompactBinding binding;

        ViewHolder(ItemActivityCompactBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TourActivity activity) {
            binding.tvCompactName.setText(activity.getName());
            binding.tvCompactPrice.setText(
                    String.format(Locale.getDefault(), "$%.0f", activity.getPrice()));
            if (activity.getImageUrl() != null) {
                Glide.with(binding.getRoot().getContext())
                        .load(activity.getImageUrl())
                        .centerCrop()
                        .into(binding.ivCompactImage);
            }
            binding.getRoot().setOnClickListener(v -> listener.onActivityClick(activity));
        }
    }
}
