package com.uade.xplorenow.ui.activities;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uade.xplorenow.R;
import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.databinding.ItemActivityBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    public interface OnActivityClickListener {
        void onActivityClick(TourActivity activity);
    }

    private List<TourActivity> activities = new ArrayList<>();
    private OnActivityClickListener listener;

    public ActivityAdapter(OnActivityClickListener listener) {
        this.listener = listener;
    }

    public void setActivities(List<TourActivity> activities) {
        this.activities = activities != null ? activities : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActivityBinding binding = ItemActivityBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(activities.get(position));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemActivityBinding binding;

        ViewHolder(ItemActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TourActivity activity) {
            binding.tvActivityName.setText(activity.getName());
            binding.tvActivityPrice.setText(
                    String.format(Locale.getDefault(), "$%.2f", activity.getPrice()));
            int spots = activity.getAvailableSpots();
            if (spots <= 0) {
                binding.tvActivityCapacity.setText("Sin cupos");
                binding.tvActivityCapacity.setTextColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_cancelled));
            } else if (spots <= 5) {
                binding.tvActivityCapacity.setText(
                        String.format(Locale.getDefault(), "¡Últimos %d cupos!", spots));
                binding.tvActivityCapacity.setTextColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_pending));
            } else {
                binding.tvActivityCapacity.setText(
                        String.format(Locale.getDefault(), "%d cupos disponibles", spots));
                binding.tvActivityCapacity.setTextColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.on_surface_variant));
            }
            binding.chipCategory.setText(formatCategory(activity.getCategory()));

            if (activity.getImageUrl() != null && !activity.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(activity.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(binding.ivActivityImage);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onActivityClick(activity);
            });
        }

        private String formatCategory(String category) {
            if (category == null) return "Tour";
            switch (category) {
                case "free_tour": return "Free Tour";
                case "excursion": return "Excursión";
                case "experience": return "Experiencia";
                default: return "Tour";
            }
        }
    }
}
