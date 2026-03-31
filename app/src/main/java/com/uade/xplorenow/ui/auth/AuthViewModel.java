package com.uade.xplorenow.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.model.User;
import com.uade.xplorenow.data.remote.dto.LoginResponse;
import com.uade.xplorenow.data.repository.AuthRepository;
import com.uade.xplorenow.util.Resource;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    public AuthViewModel() {
        repository = AuthRepository.getInstance();
    }

    public LiveData<Resource<LoginResponse>> login(String email, String password) {
        return repository.login(email, password);
    }

    public LiveData<Resource<User>> register(String name, String email, String password) {
        return repository.register(name, email, password);
    }
}
