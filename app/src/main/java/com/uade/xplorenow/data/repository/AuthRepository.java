package com.uade.xplorenow.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.uade.xplorenow.data.model.User;
import com.uade.xplorenow.data.remote.ApiService;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.ForgotPasswordRequest;
import com.uade.xplorenow.data.remote.dto.LoginRequest;
import com.uade.xplorenow.data.remote.dto.LoginResponse;
import com.uade.xplorenow.data.remote.dto.MessageResponse;
import com.uade.xplorenow.data.remote.dto.OtpRequest;
import com.uade.xplorenow.data.remote.dto.RegisterRequest;
import com.uade.xplorenow.data.remote.dto.ResetPasswordRequest;
import com.uade.xplorenow.util.Resource;

import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

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
                        } else if (response.code() == 403) {
                            String backendMsg = parseErrorMessage(response);
                            if ("EMAIL_NOT_VERIFIED".equals(backendMsg)) {
                                result.setValue(Resource.error("EMAIL_NOT_VERIFIED"));
                            } else {
                                result.setValue(Resource.error("Acceso denegado"));
                            }
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

    public LiveData<Resource<MessageResponse>> verifyEmail(String email, String code) {
        MutableLiveData<Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.verifyEmail(new OtpRequest(email, code, "email_verification"))
                .enqueue(new Callback<ApiResponse<MessageResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MessageResponse>> call,
                                           Response<ApiResponse<MessageResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Código inválido o expirado"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MessageResponse>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }

    public LiveData<Resource<MessageResponse>> resendOtp(String email, String type) {
        MutableLiveData<Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.resendOtp(new OtpRequest(email, type))
                .enqueue(new Callback<ApiResponse<MessageResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MessageResponse>> call,
                                           Response<ApiResponse<MessageResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("No se pudo reenviar el código"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MessageResponse>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }

    public LiveData<Resource<MessageResponse>> forgotPassword(String email) {
        MutableLiveData<Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.forgotPassword(new ForgotPasswordRequest(email))
                .enqueue(new Callback<ApiResponse<MessageResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MessageResponse>> call,
                                           Response<ApiResponse<MessageResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("No se pudo procesar la solicitud"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MessageResponse>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }

    public LiveData<Resource<MessageResponse>> resetPassword(String email, String code, String newPassword) {
        MutableLiveData<Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.resetPassword(new ResetPasswordRequest(email, code, newPassword))
                .enqueue(new Callback<ApiResponse<MessageResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MessageResponse>> call,
                                           Response<ApiResponse<MessageResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Código inválido o expirado"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MessageResponse>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }

    private String parseErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String body = response.errorBody().string();
                JSONObject json = new JSONObject(body);
                return json.optString("message", "");
            }
        } catch (IOException | org.json.JSONException ignored) {
        }
        return "";
    }
}
