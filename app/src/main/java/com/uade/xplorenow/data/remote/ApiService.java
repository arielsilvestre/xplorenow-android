package com.uade.xplorenow.data.remote;

import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.data.model.User;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.CreateReservationRequest;
import com.uade.xplorenow.data.remote.dto.LoginRequest;
import com.uade.xplorenow.data.remote.dto.LoginResponse;
import com.uade.xplorenow.data.remote.dto.RegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // --- Auth ---
    @POST("api/v1/auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("api/v1/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @GET("api/v1/auth/me")
    Call<ApiResponse<User>> getMe();

    // --- Activities ---
    @GET("api/v1/activities")
    Call<ApiResponse<List<TourActivity>>> getActivities();

    @GET("api/v1/activities/{id}")
    Call<ApiResponse<TourActivity>> getActivityById(@Path("id") String id);

    // --- Reservations (requieren auth — el token se agrega via AuthInterceptor) ---
    @GET("api/v1/reservations/me")
    Call<ApiResponse<List<Reservation>>> getMyReservations();

    @POST("api/v1/reservations")
    Call<ApiResponse<Reservation>> createReservation(@Body CreateReservationRequest request);
}
