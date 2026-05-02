package com.uade.xplorenow.ui.destinations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.uade.xplorenow.R;
import com.uade.xplorenow.data.model.Destination;
import com.uade.xplorenow.data.model.FavoritesData;
import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.databinding.FragmentDestinationDetailBinding;
import com.uade.xplorenow.ui.activities.ActivityAdapter;
import com.uade.xplorenow.ui.favorites.FavoriteViewModel;

import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DestinationDetailFragment extends Fragment {

    private FragmentDestinationDetailBinding binding;
    private DestinationViewModel viewModel;
    private FavoriteViewModel favoriteViewModel;
    private ActivityAdapter activityAdapter;

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
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        String destinationId = DestinationDetailFragmentArgs.fromBundle(getArguments()).getDestinationId();

        // Adapter de actividades — tap navega al detalle
        activityAdapter = new ActivityAdapter(activity -> {
            Bundle args = new Bundle();
            args.putString("activityId", activity.getId());
            Navigation.findNavController(view)
                    .navigate(R.id.action_destinationDetail_to_activityDetail, args);
        });
        binding.rvDestinationActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDestinationActivities.setAdapter(activityAdapter);

        // Estado del favorito
        setupFavoriteButton(destinationId);

        viewModel.getDestinationById(destinationId).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    binding.progress.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progress.setVisibility(View.GONE);
                    if (result.getData() != null) {
                        Destination destination = result.getData();
                        binding.tvDestinationName.setText(destination.getName());
                        binding.tvDestinationDescription.setText(
                                destination.getDescription() != null
                                        ? destination.getDescription()
                                        : "Sin descripción disponible");

                        if (destination.getLatitude() != null && destination.getLongitude() != null) {
                            binding.llCoordinates.setVisibility(View.VISIBLE);
                            binding.chipCoordinates.setText(String.format(Locale.getDefault(),
                                    "%.4f, %.4f",
                                    destination.getLatitude(),
                                    destination.getLongitude()));
                        }

                        if (destination.getImageUrl() != null) {
                            Glide.with(this)
                                    .load(destination.getImageUrl())
                                    .centerCrop()
                                    .into(binding.ivDestinationImage);
                        }

                        // Mostrar actividades si las hay
                        List<TourActivity> activities = destination.getActivities();
                        if (activities != null && !activities.isEmpty()) {
                            activityAdapter.setActivities(activities);
                            binding.cardActivities.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case ERROR:
                    binding.progress.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void setupFavoriteButton(String destinationId) {
        favoriteViewModel.getMyFavorites().observe(getViewLifecycleOwner(), result -> {
            if (result.getData() != null) {
                FavoritesData data = result.getData();
                boolean isFav = false;
                if (data.getDestinations() != null) {
                    for (Destination d : data.getDestinations()) {
                        if (destinationId.equals(d.getId())) {
                            isFav = true;
                            break;
                        }
                    }
                }
                setFavoriteIcon(isFav);
            }
        });

        binding.btnFavorite.setOnClickListener(v -> {
            boolean currentlyFav = Boolean.TRUE.equals(binding.btnFavorite.getTag());
            favoriteViewModel.toggleFavoriteDestination(destinationId).observe(getViewLifecycleOwner(), result -> {
                switch (result.getStatus()) {
                    case SUCCESS:
                        setFavoriteIcon(!currentlyFav);
                        break;
                    case ERROR:
                        Snackbar.make(binding.getRoot(), "Error al actualizar favorito", Snackbar.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            });
        });
    }

    private void setFavoriteIcon(boolean isFavorite) {
        binding.btnFavorite.setTag(isFavorite);
        binding.btnFavorite.setImageResource(
                isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
