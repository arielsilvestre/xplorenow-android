package com.uade.xplorenow.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.uade.xplorenow.R;
import com.uade.xplorenow.databinding.FragmentRegistrationSuccessBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegistrationSuccessFragment extends Fragment {

    private FragmentRegistrationSuccessBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationSuccessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bloquear back — el usuario debe usar el botón
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateToLogin();
                    }
                });

        binding.btnGoToLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void navigateToLogin() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_registrationSuccess_to_login);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
