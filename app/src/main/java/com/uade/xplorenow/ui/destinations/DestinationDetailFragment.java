package com.uade.xplorenow.ui.destinations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.uade.xplorenow.databinding.FragmentDestinationDetailBinding;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DestinationDetailFragment extends Fragment {

    private FragmentDestinationDetailBinding binding;
    private DestinationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDestinationDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DestinationViewModel.class);

        String destinationId = DestinationDetailFragmentArgs.fromBundle(getArguments()).getDestinationId();

        viewModel.getDestinationById(destinationId).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    binding.progress.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progress.setVisibility(View.GONE);
                    if (result.getData() != null) {
                        binding.tvDestinationName.setText(result.getData().getName());
                        binding.tvDestinationDescription.setText(
                                result.getData().getDescription() != null
                                        ? result.getData().getDescription()
                                        : "Sin descripción disponible");

                        if (result.getData().getLatitude() != null
                                && result.getData().getLongitude() != null) {
                            binding.llCoordinates.setVisibility(View.VISIBLE);
                            binding.chipCoordinates.setText(String.format(Locale.getDefault(),
                                    "%.4f, %.4f",
                                    result.getData().getLatitude(),
                                    result.getData().getLongitude()));
                        }

                        if (result.getData().getImageUrl() != null) {
                            Glide.with(this)
                                    .load(result.getData().getImageUrl())
                                    .centerCrop()
                                    .into(binding.ivDestinationImage);
                        }
                    }
                    break;
                case ERROR:
                    binding.progress.setVisibility(View.GONE);
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
