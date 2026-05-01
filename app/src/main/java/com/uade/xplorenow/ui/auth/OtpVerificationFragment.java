package com.uade.xplorenow.ui.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.uade.xplorenow.databinding.FragmentOtpVerificationBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OtpVerificationFragment extends Fragment {

    private FragmentOtpVerificationBinding binding;
    private AuthViewModel viewModel;
    private String email;
    private String otpType;
    private CountDownTimer resendTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        OtpVerificationFragmentArgs args = OtpVerificationFragmentArgs.fromBundle(requireArguments());
        email = args.getEmail();
        otpType = args.getOtpType();

        binding.tvSubtitle.setText("Te enviamos un código a " + email);

        binding.btnVerify.setOnClickListener(v -> attemptVerify());
        binding.btnResend.setOnClickListener(v -> resendOtp());

        startResendTimer();
    }

    private void attemptVerify() {
        String code = binding.etCode.getText() != null
                ? binding.etCode.getText().toString().trim() : "";

        if (code.length() != 6) {
            showError("El código debe tener 6 dígitos");
            return;
        }

        hideError();

        if ("email_verification".equals(otpType)) {
            viewModel.verifyEmail(email, code).observe(getViewLifecycleOwner(), result -> {
                switch (result.getStatus()) {
                    case LOADING:
                        setLoading(true);
                        break;
                    case SUCCESS:
                        setLoading(false);
                        Toast.makeText(requireContext(),
                                "¡Email verificado! Ya podés iniciar sesión.", Toast.LENGTH_LONG).show();
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_otpVerification_to_login);
                        break;
                    case ERROR:
                        setLoading(false);
                        showError(result.getMessage());
                        break;
                }
            });
        } else {
            // password_reset: navegar a ResetPassword pasando email y code
            OtpVerificationFragmentDirections.ActionOtpVerificationToResetPassword action =
                    OtpVerificationFragmentDirections.actionOtpVerificationToResetPassword(email, code);
            Navigation.findNavController(requireView()).navigate(action);
        }
    }

    private void resendOtp() {
        viewModel.resendOtp(email, otpType).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    binding.btnResend.setEnabled(false);
                    break;
                case SUCCESS:
                    Toast.makeText(requireContext(), "Código reenviado", Toast.LENGTH_SHORT).show();
                    startResendTimer();
                    break;
                case ERROR:
                    showError(result.getMessage());
                    binding.btnResend.setEnabled(true);
                    break;
            }
        });
    }

    private void startResendTimer() {
        binding.btnResend.setEnabled(false);
        resendTimer = new CountDownTimer(60_000, 1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (binding == null) return;
                binding.btnResend.setText("Reenviar (" + (millisUntilFinished / 1000) + "s)");
            }

            @Override
            public void onFinish() {
                if (binding == null) return;
                binding.btnResend.setText("Reenviar código");
                binding.btnResend.setEnabled(true);
            }
        }.start();
    }

    private void setLoading(boolean loading) {
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnVerify.setEnabled(!loading);
        binding.tvError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        binding.tvError.setText(message);
        binding.tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        binding.tvError.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (resendTimer != null) {
            resendTimer.cancel();
            resendTimer = null;
        }
        binding = null;
    }
}
