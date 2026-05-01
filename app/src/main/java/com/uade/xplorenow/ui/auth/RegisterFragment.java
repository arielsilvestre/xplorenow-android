package com.uade.xplorenow.ui.auth;

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

import com.uade.xplorenow.R;
import com.uade.xplorenow.databinding.FragmentRegisterBinding;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        binding.tvLoginLink.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_register_to_login));
    }

    private void attemptRegister() {
        String name = binding.etName.getText() != null
                ? binding.etName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString() : "";

        if (name.isEmpty()) {
            binding.tilName.setError("Ingresá tu nombre");
            return;
        }
        if (email.isEmpty()) {
            binding.tilEmail.setError("Ingresá tu email");
            return;
        }
        if (password.length() < 6) {
            binding.tilPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        binding.tilName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        viewModel.register(name, email, password).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_register_to_registrationSuccess);
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
        binding.btnRegister.setEnabled(!loading);
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
