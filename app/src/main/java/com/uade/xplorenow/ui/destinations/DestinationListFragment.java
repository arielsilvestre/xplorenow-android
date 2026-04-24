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

import com.uade.xplorenow.databinding.FragmentDestinationListBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DestinationListFragment extends Fragment {

    private FragmentDestinationListBinding binding;
    private DestinationViewModel viewModel;
    private DestinationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDestinationListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DestinationViewModel.class);

        adapter = new DestinationAdapter(destination -> {
            DestinationListFragmentDirections.ActionDestinationListToDetail action =
                    DestinationListFragmentDirections.actionDestinationListToDetail(destination.getId());
            Navigation.findNavController(view).navigate(action);
        });

        binding.rvDestinations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDestinations.setAdapter(adapter);

        viewModel.getDestinations().observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setVisibility(View.GONE);
                    binding.tvError.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    binding.progress.setVisibility(View.GONE);
                    if (result.getData() == null || result.getData().isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvEmpty.setVisibility(View.GONE);
                        adapter.setDestinations(result.getData());
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
