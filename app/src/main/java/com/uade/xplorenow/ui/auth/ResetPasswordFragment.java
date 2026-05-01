package com.uade.xplorenow.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.uade.xplorenow.R;
import com.uade.xplorenow.databinding.FragmentResetPasswordBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;
    private AuthViewModel viewModel;
    private String email;
    private String code;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        ResetPasswordFragmentArgs args = ResetPasswordFragmentArgs.fromBundle(requireArguments());
        email = args.getEmail();
        code = args.getCode();

        binding.btnReset.setOnClickListener(v -> attemptReset());
    }

    private void attemptReset() {
        String newPassword = binding.etNewPassword.getText() != null
                ? binding.etNewPassword.getText().toString() : "";
        String confirmPassword = binding.etConfirmPassword.getText() != null
                ? binding.etConfirmPassword.getText().toString() : "";

        binding.tilNewPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        if (newPassword.length() < 6) {
            binding.tilNewPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Las contraseñas no coinciden");
            return;
        }

        viewModel.resetPassword(email, code, newPassword).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    Toast.makeText(requireContext(),
                            "Contraseña actualizada. Ya podés iniciar sesión.", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_resetPassword_to_login);
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
        binding.btnReset.setEnabled(!loading);
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
