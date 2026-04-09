package com.uade.xplorenow.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.uade.xplorenow.data.model.User;
import com.uade.xplorenow.data.remote.ApiService;

import javax.inject.Inject;
import javax.inject.Singleton;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.LoginRequest;
import com.uade.xplorenow.data.remote.dto.LoginResponse;
import com.uade.xplorenow.data.remote.dto.RegisterRequest;
import com.uade.xplorenow.util.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRepository {

    private final ApiService apiService;

    @Inject
    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<LoginResponse>> login(String email, String password) {
        MutableLiveData<Resource<LoginResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.login(new LoginRequest(email, password))
                .enqueue(new Callback<ApiResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<LoginResponse>> call,
                                           Response<ApiResponse<LoginResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            String msg = response.code() == 401
                                    ? "Email o contraseña incorrectos"
                                    : "Error al iniciar sesión";
                            result.setValue(Resource.error(msg));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }

    public LiveData<Resource<User>> register(String name, String email, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.register(new RegisterRequest(name, email, password))
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call,
                                           Response<ApiResponse<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            String msg = response.code() == 409
                                    ? "El email ya está registrado"
                                    : "Error al crear la cuenta";
                            result.setValue(Resource.error(msg));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }
}
