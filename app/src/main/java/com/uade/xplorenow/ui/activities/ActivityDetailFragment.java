package com.uade.xplorenow.ui.activities;

import dagger.hilt.android.AndroidEntryPoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.uade.xplorenow.R;
import com.uade.xplorenow.databinding.FragmentActivityDetailBinding;
import java.util.Locale;

@AndroidEntryPoint
public class ActivityDetailFragment extends Fragment {

    private FragmentActivityDetailBinding binding;
    private ActivityViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentActivityDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        // Recibir el activityId via SafeArgs
        String activityId = ActivityDetailFragmentArgs.fromBundle(getArguments()).getActivityId();

        viewModel.getActivityById(activityId).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.btnReserve.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progress.setVisibility(View.GONE);
                    binding.btnReserve.setEnabled(true);
                    if (result.getData() != null) {
                        binding.tvDetailName.setText(result.getData().getName());
                        binding.tvDetailPrice.setText(
                                String.format(Locale.getDefault(), "$%.2f", result.getData().getPrice()));
                        binding.tvDetailCapacity.setText(
                                String.format(Locale.getDefault(), "%d personas", result.getData().getCapacity()));
                        binding.tvDetailDescription.setText(
                                result.getData().getDescription() != null
                                        ? result.getData().getDescription()
                                        : "Sin descripción disponible");
                        binding.chipCategory.setText(result.getData().getCategory());

                        if (result.getData().getImageUrl() != null) {
                            Glide.with(this)
                                    .load(result.getData().getImageUrl())
                                    .centerCrop()
                                    .into(binding.ivDetailImage);
                        }

                        binding.btnReserve.setOnClickListener(v -> {
                            ActivityDetailFragmentDirections.ActionActivityDetailToReservationCreate action =
                                    ActivityDetailFragmentDirections.actionActivityDetailToReservationCreate(activityId);
                            Navigation.findNavController(v).navigate(action);
                        });
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
