package com.uade.xplorenow.ui.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
                    String.format(Locale.getDefault(), "USD %.0f", activity.getPrice()));
            binding.tvActivityCapacity.setText(
                    String.format(Locale.getDefault(), "%02d personas", activity.getCapacity()));

            // Badge de categoría: texto + color según tipo
            String category = activity.getCategory();
            binding.chipCategory.setText(formatCategory(category));
            binding.chipCategory.setChipBackgroundColor(
                    ColorStateList.valueOf(getCategoryColor(category)));
            binding.chipCategory.setTextColor(Color.WHITE);

            if (activity.getImageUrl() != null && !activity.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(activity.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.bg_gradient_header)
                        .into(binding.ivActivityImage);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onActivityClick(activity);
            });
        }

        private String formatCategory(String category) {
            if (category == null) return "Tour";
            switch (category) {
                case "free_tour":  return "Free Tour";
                case "excursion":  return "Excursión";
                case "experience": return "Experiencia";
                default:           return "Tour";
            }
        }

        /** Color ARGB del chip según categoría (aprox. 88% opacidad). */
        private int getCategoryColor(String category) {
            if (category == null) return Color.argb(224, 34, 197, 94);
            switch (category) {
                case "free_tour":  return Color.argb(204, 17,  24,  39); // grafito oscuro
                case "excursion":  return Color.argb(224, 255, 138,  0); // naranja
                case "experience": return Color.argb(224, 139, 92,  246); // violeta
                default:           return Color.argb(224, 34,  197, 94); // verde
            }
        }
    }
}
