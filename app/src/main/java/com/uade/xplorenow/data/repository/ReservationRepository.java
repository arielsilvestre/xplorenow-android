package com.uade.xplorenow.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.data.remote.ApiClient;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.CreateReservationRequest;
import com.uade.xplorenow.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationRepository {

    private static ReservationRepository instance;

    private ReservationRepository() {}

    public static ReservationRepository getInstance() {
        if (instance == null) {
            instance = new ReservationRepository();
        }
        return instance;
    }

    public LiveData<Resource<List<Reservation>>> getMyReservations() {
        MutableLiveData<Resource<List<Reservation>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        ApiClient.getInstance().getService().getMyReservations()
                .enqueue(new Callback<ApiResponse<List<Reservation>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Reservation>>> call,
                                           Response<ApiResponse<List<Reservation>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Error al cargar reservas"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Reservation>>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }

    public LiveData<Resource<Reservation>> createReservation(String activityId, String date, int people) {
        MutableLiveData<Resource<Reservation>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        CreateReservationRequest request = new CreateReservationRequest(activityId, date, people);

        ApiClient.getInstance().getService().createReservation(request)
                .enqueue(new Callback<ApiResponse<Reservation>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Reservation>> call,
                                           Response<ApiResponse<Reservation>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Error al crear la reserva"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Reservation>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }
}
