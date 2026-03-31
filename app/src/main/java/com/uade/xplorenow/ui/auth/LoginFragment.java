package com.uade.xplorenow.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.uade.xplorenow.R;
import com.uade.xplorenow.data.local.SessionManager;
import com.uade.xplorenow.databinding.FragmentLoginBinding;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();

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

        binding.tvRegisterLink.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_login_to_register);
        });

        // Biometría: ofrecer si hay token guardado y el dispositivo lo soporta
        checkBiometricAvailability();
    }

    /**
     * Verifica si el dispositivo soporta biometría y si hay una sesión guardada.
     * Si ambas condiciones se cumplen, muestra el prompt biométrico automáticamente.
     * (Semana 6: Biometría y Sesiones)
     */
    private void checkBiometricAvailability() {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        boolean canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS;

        if (!canAuthenticate) return;

        // Revisar si hay sesión guardada
        SessionManager session = SessionManager.getInstance(requireContext());
        disposables.add(
            session.getToken()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    token -> {
                        if (!token.isEmpty()) {
                            showBiometricPrompt();
                        }
                    },
                    error -> { /* sin sesión, no mostramos biometría */ }
                )
        );
    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Iniciar sesión con biometría")
                .setSubtitle("Usá tu huella o Face ID para ingresar")
                .setNegativeButtonText("Usar contraseña")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(
                this,
                ContextCompat.getMainExecutor(requireContext()),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        // Autenticación exitosa → navegar a Home sin re-login
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_login_to_home);
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        // Usuario canceló o falló → dejar el formulario visible
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        showError("Biometría no reconocida. Intentá de nuevo.");
                    }
                }
        );

        biometricPrompt.authenticate(promptInfo);
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
                    SessionManager session = SessionManager.getInstance(requireContext());
                    com.uade.xplorenow.data.model.User loginUser = result.getData().getUser();
                    Log.d("XploreNow", "Login SUCCESS — token=" + result.getData().getToken()
                            + " user=" + loginUser);
                    if (loginUser == null) {
                        loginUser = new com.uade.xplorenow.data.model.User("", "", "", "user");
                    }
                    final com.uade.xplorenow.data.model.User finalUser = loginUser;
                    disposables.add(
                        session.saveSession(result.getData().getToken(), finalUser)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                prefs -> {
                                    NavController navController = Navigation.findNavController(requireView());
                                    navController.navigate(R.id.action_login_to_home);
                                },
                                error -> {
                                    Log.e("XploreNow", "saveSession FAILED", error);
                                    showError("Error al guardar sesión: "
                                            + error.getClass().getSimpleName()
                                            + " — " + error.getMessage());
                                }
                            )
                    );
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
        disposables.clear();
        binding = null;
    }
}
