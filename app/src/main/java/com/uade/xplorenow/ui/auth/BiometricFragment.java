package com.uade.xplorenow.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.fragment.app.Fragment;

import com.uade.xplorenow.data.local.TokenManager;
import com.uade.xplorenow.databinding.FragmentBiometricBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BiometricFragment extends Fragment {

    @Inject
    TokenManager tokenManager;

    private FragmentBiometricBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBiometricBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkBiometricStatus();
    }

    private void checkBiometricStatus() {
        BiometricManager manager = BiometricManager.from(requireContext());
        int status = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        binding.btnGoToSettings.setVisibility(View.GONE);
        binding.btnToggleBiometric.setVisibility(View.GONE);

        switch (status) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                binding.tvBiometricStatus.setText(
                        "Tu dispositivo tiene credenciales biometricas enroladas y listas para usar.");
                binding.btnToggleBiometric.setVisibility(View.VISIBLE);
                refreshToggleButton();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                binding.tvBiometricStatus.setText(
                        "Este dispositivo no tiene hardware biometrico.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                binding.tvBiometricStatus.setText(
                        "El sensor biometrico no esta disponible en este momento.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                binding.tvBiometricStatus.setText(
                        "No hay huella o Face ID registrados. Configura uno en los ajustes del dispositivo.");
                binding.btnGoToSettings.setVisibility(View.VISIBLE);
                break;
        }

        binding.btnGoToSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
            startActivity(intent);
        });

        binding.btnToggleBiometric.setOnClickListener(v -> {
            boolean newState = !tokenManager.isBiometricEnabled();
            tokenManager.setBiometricEnabled(newState);
            if (!newState) {
                tokenManager.saveEncryptedToken(null);
            }
            refreshToggleButton();
        });
    }

    private void refreshToggleButton() {
        boolean enabled = tokenManager.isBiometricEnabled();
        binding.btnToggleBiometric.setText(
                enabled ? "Desactivar inicio biometrico" : "Activar inicio biometrico");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
