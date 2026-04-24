package com.uade.xplorenow.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.uade.xplorenow.data.model.Destination;
import com.uade.xplorenow.data.remote.ApiService;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.util.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class DestinationRepository {

    private final ApiService apiService;

    @Inject
    public DestinationRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<List<Destination>>> getDestinations() {
        MutableLiveData<Resource<List<Destination>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getDestinations()
                .enqueue(new Callback<ApiResponse<List<Destination>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Destination>>> call,
                                           Response<ApiResponse<List<Destination>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Error al cargar destinos"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Destination>>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }

    public LiveData<Resource<Destination>> getDestinationById(String id) {
        MutableLiveData<Resource<Destination>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getDestinationById(id)
                .enqueue(new Callback<ApiResponse<Destination>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Destination>> call,
                                           Response<ApiResponse<Destination>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Destino no encontrado"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Destination>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }
}
