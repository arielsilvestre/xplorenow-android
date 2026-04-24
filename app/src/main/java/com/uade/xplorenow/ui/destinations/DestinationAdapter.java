package com.uade.xplorenow.ui.destinations;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uade.xplorenow.R;
import com.uade.xplorenow.data.model.Destination;
import com.uade.xplorenow.databinding.ItemDestinationBinding;

import java.util.ArrayList;
import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {

    public interface OnDestinationClickListener {
        void onDestinationClick(Destination destination);
    }

    private List<Destination> destinations = new ArrayList<>();
    private final OnDestinationClickListener listener;

    public DestinationAdapter(OnDestinationClickListener listener) {
        this.listener = listener;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations != null ? destinations : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDestinationBinding binding = ItemDestinationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(destinations.get(position));
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDestinationBinding binding;

        ViewHolder(ItemDestinationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Destination destination) {
            binding.tvDestinationName.setText(destination.getName());

            String desc = destination.getDescription();
            binding.tvDestinationDescription.setText(
                    desc != null && desc.length() > 80
                            ? desc.substring(0, 80) + "…"
                            : desc != null ? desc : "");

            if (destination.getImageUrl() != null && !destination.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(destination.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.bg_gradient_header)
                        .into(binding.ivDestinationImage);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onDestinationClick(destination);
            });
        }
    }
}
