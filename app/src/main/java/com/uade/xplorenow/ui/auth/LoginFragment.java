package com.uade.xplorenow.ui.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

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

    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private int currentPage = 0;
    private static final int TOTAL_SLIDES = 5;
    private static final long AUTO_SCROLL_DELAY_MS = 4000L;

    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (binding == null) return;
            currentPage = (currentPage + 1) % TOTAL_SLIDES;
            binding.vpLandscapes.setCurrentItem(currentPage, true);
            autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY_MS);
        }
    };

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

        setupLandscapes();
        setupDots();

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.tvRegisterLink.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_login_to_register));
        binding.tvForgotPassword.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_login_to_forgotPassword));

        if (tokenManager.isBiometricEnabled() && tokenManager.getEncryptedToken() != null) {
            showBiometricPrompt();
        }
    }

    private void setupLandscapes() {
        binding.vpLandscapes.setAdapter(new LandscapeSlideAdapter());

        binding.vpLandscapes.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateDots(position);
            }
        });

        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY_MS);
    }

    private void setupDots() {
        binding.llDots.removeAllViews();
        int dotMarginDp = (int) (6 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < TOTAL_SLIDES; i++) {
            ImageView dot = new ImageView(requireContext());
            dot.setImageResource(i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(dotMarginDp / 2, 0, dotMarginDp / 2, 0);
            dot.setLayoutParams(params);

            binding.llDots.addView(dot);
        }
    }

    private void updateDots(int activePosition) {
        if (binding == null) return;
        for (int i = 0; i < binding.llDots.getChildCount(); i++) {
            ImageView dot = (ImageView) binding.llDots.getChildAt(i);
            dot.setImageResource(i == activePosition ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("XploreNow")
                .setSubtitle("Verificá tu identidad para continuar")
                .setNegativeButtonText("Usar contraseña")
                .build();

        BiometricPrompt prompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(requireContext()),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        String token = tokenManager.getEncryptedToken();
                        tokenManager.saveToken(token);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_login_to_home);
                    }

                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        // Fallback — el formulario ya está visible
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        showError("Biometria no reconocida. Intentá de nuevo o usá tu contraseña.");
                    }
                });

        prompt.authenticate(promptInfo);
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
                    offerBiometricIfAvailable(token);
                    break;
                case ERROR:
                    setLoading(false);
                    if ("EMAIL_NOT_VERIFIED".equals(result.getMessage())) {
                        LoginFragmentDirections.ActionLoginToOtpVerification action =
                                LoginFragmentDirections.actionLoginToOtpVerification(email, "email_verification");
                        Navigation.findNavController(requireView()).navigate(action);
                    } else {
                        showError(result.getMessage());
                    }
                    break;
            }
        });
    }

    private void offerBiometricIfAvailable(String token) {
        BiometricManager manager = BiometricManager.from(requireContext());
        boolean canAuth = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                == BiometricManager.BIOMETRIC_SUCCESS;

        if (!canAuth || tokenManager.isBiometricEnabled()) {
            Navigation.findNavController(requireView()).navigate(R.id.action_login_to_home);
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Inicio rapido")
                .setMessage("Queres activar el inicio con huella o Face ID para la proxima vez?")
                .setPositiveButton("Activar", (dialog, which) -> {
                    tokenManager.setBiometricEnabled(true);
                    tokenManager.saveEncryptedToken(token);
                    Navigation.findNavController(requireView()).navigate(R.id.action_login_to_home);
                })
                .setNegativeButton("Ahora no", (dialog, which) ->
                        Navigation.findNavController(requireView()).navigate(R.id.action_login_to_home))
                .show();
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
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
        binding = null;
    }
}
