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
import com.uade.xplorenow.R;
import com.uade.xplorenow.databinding.FragmentProfileBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private static final String PREFS_PROFILE = "xplorenow_profile";
    private static final String KEY_PHOTO_URI = "profile_photo_uri";

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    // Launcher para pedir permiso
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    pickImageLauncher.launch("image/*");
                }
            });

    // Launcher para abrir la galería
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

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
            }
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

        // Foto de perfil — abrir galería al tocar
        binding.ivProfilePhoto.setOnClickListener(v -> openGallery());
        binding.tvChangePhoto.setOnClickListener(v -> openGallery());
        // Botón biométrico — solo si existe en el layout (agregado en Task 2)
        if (binding.btnBiometric != null) {
            binding.btnBiometric.setOnClickListener(v ->
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_profile_to_biometric));
        }
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
