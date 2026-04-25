package com.uade.xplorenow.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.uade.xplorenow.R;
import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.databinding.FragmentProfileBinding;
import com.uade.xplorenow.util.Resource;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private static final String PREFS_PROFILE = "xplorenow_profile";
    private static final String KEY_PHOTO_URI = "profile_photo_uri";

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    // Launcher para abrir la galería
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    // Launcher para pedir permiso
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    pickImageLauncher.launch("image/*");
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvName.setText(user.getName() != null ? user.getName() : "—");
                binding.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "—");
                // Restaurar chips de preferencias
                if (user.getPreferences() != null) {
                    binding.chipPrefTour.setChecked(user.getPreferences().contains("tour"));
                    binding.chipPrefFreeTour.setChecked(user.getPreferences().contains("free_tour"));
                    binding.chipPrefExcursion.setChecked(user.getPreferences().contains("excursion"));
                    binding.chipPrefExperience.setChecked(user.getPreferences().contains("experience"));
                }
            }
        });

        binding.btnSavePreferences.setOnClickListener(v -> {
            List<String> selected = new ArrayList<>();
            if (binding.chipPrefTour.isChecked()) selected.add("tour");
            if (binding.chipPrefFreeTour.isChecked()) selected.add("free_tour");
            if (binding.chipPrefExcursion.isChecked()) selected.add("excursion");
            if (binding.chipPrefExperience.isChecked()) selected.add("experience");
            viewModel.updatePreferences(selected);
        });

        viewModel.getPreferencesSaved().observe(getViewLifecycleOwner(), ok -> {
            if (ok == null) return;
            Snackbar.make(binding.getRoot(),
                    ok ? "Preferencias guardadas" : "Error al guardar. Sin conexión.",
                    Snackbar.LENGTH_SHORT).show();
        });

        viewModel.getLoggedOut().observe(getViewLifecycleOwner(), loggedOut -> {
            if (Boolean.TRUE.equals(loggedOut)) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_profile_to_login);
            }
        });

        binding.btnLogout.setOnClickListener(v -> viewModel.logout());
        binding.ivProfilePhoto.setOnClickListener(v -> requestGalleryPermission());
        binding.tvChangePhoto.setOnClickListener(v -> requestGalleryPermission());

        // Cargar foto guardada si existe
        loadSavedPhoto();

        binding.btnBiometric.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_profile_to_biometric));

        binding.btnFavorites.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_profile_to_favorites));

        loadReservationSummary();
    }

    private void loadReservationSummary() {
        viewModel.getMyReservations().observe(getViewLifecycleOwner(), result -> {
            if (result.getStatus() != Resource.Status.SUCCESS || result.getData() == null) return;

            List<Reservation> list = result.getData();
            int total = list.size();
            int confirmed = 0, cancelled = 0;
            for (Reservation r : list) {
                if ("confirmed".equals(r.getStatus())) confirmed++;
                else if ("cancelled".equals(r.getStatus())) cancelled++;
            }
            binding.tvReservationsTotal.setText(String.valueOf(total));
            binding.tvReservationsConfirmed.setText(String.valueOf(confirmed));
            binding.tvReservationsCancelled.setText(String.valueOf(cancelled));
        });
    }

    private void requestGalleryPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? android.Manifest.permission.READ_MEDIA_IMAGES
                : android.Manifest.permission.READ_EXTERNAL_STORAGE;
        permissionLauncher.launch(permission);
    }

    private void onImagePicked(@Nullable Uri imageUri) {
        if (imageUri == null || binding == null) return;

        // Persistir URI en SharedPreferences
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PHOTO_URI, imageUri.toString()).apply();

        displayPhoto(imageUri);
    }

    private void loadSavedPhoto() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
        String uriString = prefs.getString(KEY_PHOTO_URI, null);
        if (uriString != null) {
            displayPhoto(Uri.parse(uriString));
        }
    }

    private void displayPhoto(Uri uri) {
        Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.ivProfilePhoto);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
