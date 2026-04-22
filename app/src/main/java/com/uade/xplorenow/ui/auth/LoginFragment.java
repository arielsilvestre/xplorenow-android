package com.uade.xplorenow.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.uade.xplorenow.R;
import com.uade.xplorenow.data.local.TokenManager;
import com.uade.xplorenow.data.model.User;
import com.uade.xplorenow.databinding.FragmentLoginBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    @Inject
    TokenManager tokenManager;

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.tvRegisterLink.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_login_to_register));
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString() : "";

        if (email.isEmpty()) {
            binding.tilEmail.setError("Ingresá tu email");
            return;
        }
        if (password.isEmpty()) {
            binding.tilPassword.setError("Ingresá tu contraseña");
            return;
        }

        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        viewModel.login(email, password).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    User user = result.getData().getUser();
                    String token = result.getData().getToken();
                    if (user == null) user = new User("", "", "", "user");
                    tokenManager.saveToken(token);
                    tokenManager.saveUser(user.getId(), user.getName(),
                            user.getEmail(), user.getRole());
                    Log.d("XploreNow", "Login OK — user=" + user.getEmail());
                    Navigation.findNavController(requireView()).navigate(R.id.action_login_to_home);
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
        binding.btnLogin.setEnabled(!loading);
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
