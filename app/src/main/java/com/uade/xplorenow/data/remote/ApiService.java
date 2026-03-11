package com.uade.xplorenow.data.remote;

import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.LoginRequest;
import com.uade.xplorenow.data.remote.dto.LoginResponse;
import com.uade.xplorenow.data.remote.dto.RegisterRequest;
import com.uade.xplorenow.data.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/v1/auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("api/v1/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @GET("api/v1/auth/me")
    Call<ApiResponse<User>> getMe();
}
