package com.uade.xplorenow.ui.profile;

import dagger.hilt.android.AndroidEntryPoint;

import android.net.Uri;
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

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    /**
     * Launcher para abrir la galería de imágenes del dispositivo.
     * (Semana 7: Galería / Acceso a imágenes)
     */
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

        // Foto de perfil — abrir galería al tocar
        binding.ivProfilePhoto.setOnClickListener(v -> openGallery());
        binding.tvChangePhoto.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    /**
     * Callback llamado cuando el usuario selecciona una imagen de la galería.
     * Muestra la imagen en el ImageView usando Glide.
     */
    private void onImagePicked(@Nullable Uri imageUri) {
        if (imageUri != null && binding != null) {
            Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .into(binding.ivProfilePhoto);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
