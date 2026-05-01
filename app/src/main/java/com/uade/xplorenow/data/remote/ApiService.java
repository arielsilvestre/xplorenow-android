package com.uade.xplorenow.data.remote;

import com.uade.xplorenow.data.model.Destination;
import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.data.model.User;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.CreateReservationRequest;
import com.uade.xplorenow.data.remote.dto.LoginRequest;
import com.uade.xplorenow.data.remote.dto.LoginResponse;
import com.uade.xplorenow.data.remote.dto.RegisterRequest;
import com.uade.xplorenow.data.model.Review;
import com.uade.xplorenow.data.remote.dto.CreateReviewRequest;
import com.uade.xplorenow.data.remote.dto.FavoriteToggleRequest;
import com.uade.xplorenow.data.remote.dto.ForgotPasswordRequest;
import com.uade.xplorenow.data.remote.dto.MessageResponse;
import com.uade.xplorenow.data.remote.dto.OtpRequest;
import com.uade.xplorenow.data.remote.dto.ResetPasswordRequest;
import com.uade.xplorenow.data.remote.dto.UpdateUserRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // --- Auth ---
    @POST("api/v1/auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("api/v1/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @GET("api/v1/auth/me")
    Call<ApiResponse<User>> getMe();

    @POST("api/v1/auth/verify-email")
    Call<ApiResponse<MessageResponse>> verifyEmail(@Body OtpRequest request);

    @POST("api/v1/auth/resend-otp")
    Call<ApiResponse<MessageResponse>> resendOtp(@Body OtpRequest request);

    @POST("api/v1/auth/forgot-password")
    Call<ApiResponse<MessageResponse>> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/v1/auth/reset-password")
    Call<ApiResponse<MessageResponse>> resetPassword(@Body ResetPasswordRequest request);

    @PATCH("api/v1/users/me")
    Call<ApiResponse<User>> updateMe(@Body UpdateUserRequest request);

    // --- Destinations ---
    @GET("api/v1/destinations")
    Call<ApiResponse<List<Destination>>> getDestinations();

    @GET("api/v1/destinations/{id}")
    Call<ApiResponse<Destination>> getDestinationById(@Path("id") String id);

    // --- Activities ---
    @GET("api/v1/activities")
    Call<ApiResponse<List<TourActivity>>> getActivities(@Query("category") String category);

    @GET("api/v1/activities")
    Call<ApiResponse<List<TourActivity>>> getActivities();

    @GET("api/v1/activities/{id}")
    Call<ApiResponse<TourActivity>> getActivityById(@Path("id") String id);

    // --- Reservations (requieren auth — el token se agrega via AuthInterceptor) ---
    @GET("api/v1/reservations/me")
    Call<ApiResponse<List<Reservation>>> getMyReservations();

    @GET("api/v1/reservations/history")
    Call<ApiResponse<List<Reservation>>> getReservationHistory();

    @POST("api/v1/reservations")
    Call<ApiResponse<Reservation>> createReservation(@Body CreateReservationRequest request);

    @PATCH("api/v1/reservations/{id}/cancel")
    Call<ApiResponse<Reservation>> cancelReservation(@Path("id") String id);

    // --- Reviews ---
    @POST("api/v1/reviews")
    Call<ApiResponse<Review>> createReview(@Body CreateReviewRequest request);

    // --- Favorites ---
    @POST("api/v1/favorites/toggle")
    Call<ApiResponse<Void>> toggleFavorite(@Body FavoriteToggleRequest request);

    @GET("api/v1/favorites/me")
    Call<ApiResponse<List<TourActivity>>> getMyFavorites();
}
