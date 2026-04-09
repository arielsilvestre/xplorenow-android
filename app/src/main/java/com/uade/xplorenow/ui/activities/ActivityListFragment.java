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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.uade.xplorenow.R;
import com.uade.xplorenow.databinding.FragmentActivityListBinding;

@AndroidEntryPoint
public class ActivityListFragment extends Fragment {

    private FragmentActivityListBinding binding;
    private ActivityViewModel viewModel;
    private ActivityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentActivityListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        adapter = new ActivityAdapter(activity -> {
            ActivityListFragmentDirections.ActionActivityListToDetail action =
                    ActivityListFragmentDirections.actionActivityListToDetail(activity.getId());
            Navigation.findNavController(view).navigate(action);
        });

        binding.rvActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvActivities.setAdapter(adapter);

        viewModel.getActivities().observe(getViewLifecycleOwner(), result -> {
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
                        adapter.setActivities(result.getData());
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
