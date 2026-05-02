package com.uade.xplorenow.ui.favorites;

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

import com.uade.xplorenow.data.model.FavoritesData;
import com.uade.xplorenow.databinding.FragmentFavoritesBinding;
import com.uade.xplorenow.ui.activities.ActivityAdapter;
import com.uade.xplorenow.ui.destinations.DestinationAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private FavoriteViewModel viewModel;
    private ActivityAdapter activityAdapter;
    private DestinationAdapter destinationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        activityAdapter = new ActivityAdapter(activity -> {
            FavoritesFragmentDirections.ActionFavoritesToDetail action =
                    FavoritesFragmentDirections.actionFavoritesToDetail(activity.getId());
            Navigation.findNavController(view).navigate(action);
        });

        destinationAdapter = new DestinationAdapter(destination -> {
            FavoritesFragmentDirections.ActionFavoritesToDestinationDetail action =
                    FavoritesFragmentDirections.actionFavoritesToDestinationDetail(destination.getId());
            Navigation.findNavController(view).navigate(action);
        });

        binding.rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFavorites.setAdapter(activityAdapter);

        binding.rvFavoriteDestinations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFavoriteDestinations.setAdapter(destinationAdapter);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        viewModel.getMyFavorites().observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setVisibility(View.GONE);
                    binding.tvError.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    binding.progress.setVisibility(View.GONE);
                    if (result.getData() != null) {
                        FavoritesData data = result.getData();
                        boolean hasActivities = data.getActivities() != null && !data.getActivities().isEmpty();
                        boolean hasDestinations = data.getDestinations() != null && !data.getDestinations().isEmpty();

                        if (hasActivities) {
                            activityAdapter.setActivities(data.getActivities());
                            binding.sectionActivities.setVisibility(View.VISIBLE);
                        } else {
                            binding.sectionActivities.setVisibility(View.GONE);
                        }

                        if (hasDestinations) {
                            destinationAdapter.setDestinations(data.getDestinations());
                            binding.sectionDestinations.setVisibility(View.VISIBLE);
                        } else {
                            binding.sectionDestinations.setVisibility(View.GONE);
                        }

                        binding.tvEmpty.setVisibility((!hasActivities && !hasDestinations) ? View.VISIBLE : View.GONE);
                    } else {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    binding.progress.setVisibility(View.GONE);
                    binding.tvError.setText(result.getMessage());
                    binding.tvError.setVisibility(View.VISIBLE);
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
