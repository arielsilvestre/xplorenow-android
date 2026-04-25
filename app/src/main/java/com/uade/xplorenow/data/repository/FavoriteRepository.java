package com.uade.xplorenow.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.data.remote.ApiService;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.FavoriteToggleRequest;
import com.uade.xplorenow.util.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class FavoriteRepository {

    private final ApiService apiService;

    @Inject
    public FavoriteRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<List<TourActivity>>> getMyFavorites() {
        MutableLiveData<Resource<List<TourActivity>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        apiService.getMyFavorites().enqueue(new Callback<ApiResponse<List<TourActivity>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TourActivity>>> call,
                                   Response<ApiResponse<List<TourActivity>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Error al cargar favoritos"));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<TourActivity>>> call, Throwable t) {
                result.setValue(Resource.error("Sin conexión."));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> toggleFavorite(String activityId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        apiService.toggleFavorite(new FavoriteToggleRequest(activityId))
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful()) {
                            result.setValue(Resource.success(null));
                        } else {
                            result.setValue(Resource.error("Error al actualizar favorito"));
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión."));
                    }
                });
        return result;
    }
}
