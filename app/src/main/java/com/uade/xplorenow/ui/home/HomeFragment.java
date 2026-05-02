package com.uade.xplorenow.ui.home;

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
import com.uade.xplorenow.data.local.TokenManager;
import com.uade.xplorenow.databinding.FragmentHomeBinding;
import com.uade.xplorenow.ui.activities.ActivityCompactAdapter;
import com.uade.xplorenow.ui.activities.ActivityViewModel;
import com.uade.xplorenow.ui.profile.ProfileViewModel;

import java.util.List;

import javax.inject.Inject;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    @Inject
    TokenManager tokenManager;

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String name = tokenManager.getUserName();
        String greeting = (name != null && !name.isEmpty())
                ? "¡Bienvenido, " + name + "! 👋"
                : "¡Bienvenido! 👋";
        binding.tvGreeting.setText(greeting);

        binding.cardActivities.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.activityListFragment));

        binding.cardReservations.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.reservationListFragment));

        binding.tvSeeAll.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.activityListFragment));

        setupHomeActivities();
    }

    private void setupHomeActivities() {
        ActivityCompactAdapter adapter = new ActivityCompactAdapter(activity -> {
            android.os.Bundle args = new android.os.Bundle();
            args.putString("activityId", activity.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_home_to_activityDetail, args);
        });

        binding.rvHomeActivities.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvHomeActivities.setAdapter(adapter);

        ActivityViewModel activityViewModel =
                new ViewModelProvider(this).get(ActivityViewModel.class);

        ProfileViewModel profileViewModel =
                new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        profileViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            List<String> prefs = user != null ? user.getPreferences() : null;
            if (prefs != null && !prefs.isEmpty()) {
                binding.tvSectionTitle.setText("Para vos");
            } else {
                binding.tvSectionTitle.setText("Destacadas");
            }
            activityViewModel.getActivitiesByCategories(prefs)
                    .observe(getViewLifecycleOwner(), result -> {
                        if (result.getData() != null) {
                            adapter.setActivities(result.getData());
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
