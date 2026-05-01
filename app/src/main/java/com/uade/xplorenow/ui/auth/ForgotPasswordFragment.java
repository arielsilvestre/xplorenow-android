package com.uade.xplorenow.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.uade.xplorenow.databinding.FragmentForgotPasswordBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnSend.setOnClickListener(v -> attemptForgotPassword());
    }

    private void attemptForgotPassword() {
        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";

        if (email.isEmpty()) {
            binding.tilEmail.setError("Ingresá tu email");
            return;
        }

        binding.tilEmail.setError(null);

        viewModel.forgotPassword(email).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    // Siempre navegar — el backend nunca confirma si el email existe
                    ForgotPasswordFragmentDirections.ActionForgotPasswordToOtpVerification action =
                            ForgotPasswordFragmentDirections.actionForgotPasswordToOtpVerification(
                                    email, "password_reset");
                    Navigation.findNavController(requireView()).navigate(action);
                    break;
                case ERROR:
                    setLoading(false);
                    showError(result.getMessage());
                    break;
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnSend.setEnabled(!loading);
        binding.tvError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        binding.tvError.setText(message);
        binding.tvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
