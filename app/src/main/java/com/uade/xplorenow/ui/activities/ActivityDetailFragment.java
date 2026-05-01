package com.uade.xplorenow.ui.activities;

import dagger.hilt.android.AndroidEntryPoint;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.material.snackbar.Snackbar;
import com.uade.xplorenow.R;
import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.databinding.FragmentActivityDetailBinding;
import com.uade.xplorenow.ui.reservations.ReservationViewModel;
import java.util.List;
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
        ReservationViewModel reservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

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
                        TourActivity act = result.getData();
                        binding.tvDetailName.setText(act.getName());
                        binding.tvDetailPrice.setText(
                                String.format(Locale.getDefault(), "$%.2f", act.getPrice()));
                        binding.tvDetailCapacity.setText(
                                String.format(Locale.getDefault(), "%d personas", act.getCapacity()));
                        binding.tvDetailDescription.setText(
                                act.getDescription() != null ? act.getDescription() : "Sin descripción disponible");
                        binding.chipCategory.setText(act.getCategory());

                        if (act.getImageUrl() != null) {
                            Glide.with(this)
                                    .load(act.getImageUrl())
                                    .centerCrop()
                                    .into(binding.ivDetailImage);
                        }

                        // C2 — info extendida
                        bindExtendedInfo(act);

                        // C1 — calificación (solo si el usuario ya completó esta actividad)
                        setupReviewSection(activityId, reservationViewModel);

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

    private void bindExtendedInfo(TourActivity act) {
        boolean anyVisible = false;

        if (act.getDuration() != null && !act.getDuration().isEmpty()) {
            binding.rowDuration.setVisibility(View.VISIBLE);
            binding.tvDuration.setText(act.getDuration());
            anyVisible = true;
        }
        if (act.getMeetingPoint() != null && !act.getMeetingPoint().isEmpty()) {
            binding.rowMeetingPoint.setVisibility(View.VISIBLE);
            binding.tvMeetingPoint.setText(act.getMeetingPoint());
            anyVisible = true;
        }

        Double lat = act.getDepartureLat();
        Double lng = act.getDepartureLng();
        if (lat != null && lng != null) {
            binding.rowDepartureLocation.setVisibility(View.VISIBLE);
            binding.tvMeetingPointMap.setText(act.getMeetingPoint());
            binding.rowDepartureLocation.setOnClickListener(v -> {
                String label = Uri.encode(act.getMeetingPoint() != null
                        ? act.getMeetingPoint() : "Punto de partida");
                Uri geoUri = Uri.parse("geo:" + lat + "," + lng +
                        "?q=" + lat + "," + lng + "(" + label + ")");
                Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
                startActivity(intent);
            });
            anyVisible = true;
        } else {
            binding.rowDepartureLocation.setVisibility(View.GONE);
        }

        if (act.getWhatsIncluded() != null && !act.getWhatsIncluded().isEmpty()) {
            binding.rowWhatsIncluded.setVisibility(View.VISIBLE);
            binding.tvWhatsIncluded.setText(act.getWhatsIncluded());
            anyVisible = true;
        }
        if (act.getCancellationPolicy() != null && !act.getCancellationPolicy().isEmpty()) {
            binding.rowCancellation.setVisibility(View.VISIBLE);
            binding.tvCancellationPolicy.setText(act.getCancellationPolicy());
            anyVisible = true;
        }

        if (anyVisible) binding.cardExtraInfo.setVisibility(View.VISIBLE);
    }

    private void setupReviewSection(String activityId, ReservationViewModel reservationViewModel) {
        // Mostrar card de calificación solo si el usuario tiene una reserva confirmada pasada
        reservationViewModel.getReservationHistory().observe(getViewLifecycleOwner(), historyResult -> {
            if (historyResult.getData() != null) {
                List<Reservation> history = historyResult.getData();
                boolean completedThisActivity = false;
                for (Reservation r : history) {
                    if (activityId.equals(r.getActivityId())) {
                        completedThisActivity = true;
                        break;
                    }
                }
                if (completedThisActivity) {
                    binding.cardReview.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.btnSubmitReview.setOnClickListener(v -> {
            int stars = (int) binding.ratingBar.getRating();
            if (stars == 0) {
                Snackbar.make(binding.getRoot(), "Elegí al menos 1 estrella", Snackbar.LENGTH_SHORT).show();
                return;
            }
            String comment = binding.etReviewComment.getText() != null
                    ? binding.etReviewComment.getText().toString().trim()
                    : "";

            viewModel.createReview(activityId, stars, comment).observe(getViewLifecycleOwner(), result -> {
                switch (result.getStatus()) {
                    case LOADING:
                        binding.btnSubmitReview.setEnabled(false);
                        break;
                    case SUCCESS:
                        binding.btnSubmitReview.setEnabled(true);
                        binding.ratingBar.setRating(0);
                        binding.etReviewComment.setText("");
                        binding.tvReviewFeedback.setText("¡Gracias por tu calificación!");
                        binding.tvReviewFeedback.setTextColor(
                                androidx.core.content.ContextCompat.getColor(
                                        requireContext(), R.color.status_confirmed));
                        binding.tvReviewFeedback.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        binding.btnSubmitReview.setEnabled(true);
                        binding.tvReviewFeedback.setText(result.getMessage());
                        binding.tvReviewFeedback.setTextColor(
                                androidx.core.content.ContextCompat.getColor(
                                        requireContext(), R.color.error));
                        binding.tvReviewFeedback.setVisibility(View.VISIBLE);
                        break;
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
