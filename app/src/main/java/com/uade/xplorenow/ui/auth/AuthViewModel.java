package com.uade.xplorenow.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.model.User;
import com.uade.xplorenow.data.remote.dto.LoginResponse;
import com.uade.xplorenow.data.remote.dto.MessageResponse;
import com.uade.xplorenow.data.repository.AuthRepository;
import com.uade.xplorenow.util.Resource;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    @Inject
    public AuthViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<LoginResponse>> login(String email, String password) {
        return repository.login(email, password);
    }

    public LiveData<Resource<User>> register(String name, String email, String password) {
        return repository.register(name, email, password);
    }

    public LiveData<Resource<MessageResponse>> verifyEmail(String email, String code) {
        return repository.verifyEmail(email, code);
    }

    public LiveData<Resource<MessageResponse>> resendOtp(String email, String type) {
        return repository.resendOtp(email, type);
    }

    public LiveData<Resource<MessageResponse>> forgotPassword(String email) {
        return repository.forgotPassword(email);
    }

    public LiveData<Resource<MessageResponse>> resetPassword(String email, String code, String newPassword) {
        return repository.resetPassword(email, code, newPassword);
    }
}
