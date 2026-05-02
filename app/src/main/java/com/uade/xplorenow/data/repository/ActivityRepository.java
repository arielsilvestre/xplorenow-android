package com.uade.xplorenow.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.data.remote.ApiService;

import javax.inject.Inject;
import javax.inject.Singleton;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ActivityRepository {

    private final ApiService apiService;

    @Inject
    public ActivityRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<List<TourActivity>>> getActivities() {
        return getActivities(null);
    }

    public LiveData<Resource<List<TourActivity>>> getActivities(String category) {
        MutableLiveData<Resource<List<TourActivity>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        Call<ApiResponse<List<TourActivity>>> call = (category != null && !category.isEmpty())
                ? apiService.getActivities(category)
                : apiService.getActivities();

        call.enqueue(new Callback<ApiResponse<List<TourActivity>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<TourActivity>>> c,
                                           Response<ApiResponse<List<TourActivity>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Error al cargar actividades"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<TourActivity>>> c, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }

    public LiveData<Resource<List<TourActivity>>> getActivitiesByCategories(List<String> categories) {
        MutableLiveData<Resource<List<TourActivity>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        String csv = String.join(",", categories);
        apiService.getActivitiesByCategories(csv)
                .enqueue(new Callback<ApiResponse<List<TourActivity>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<TourActivity>>> c,
                                           Response<ApiResponse<List<TourActivity>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Error al cargar actividades"));
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<TourActivity>>> c, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });
        return result;
    }

    public LiveData<Resource<TourActivity>> getActivityById(String id) {
        MutableLiveData<Resource<TourActivity>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getActivityById(id)
                .enqueue(new Callback<ApiResponse<TourActivity>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<TourActivity>> call,
                                           Response<ApiResponse<TourActivity>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Actividad no encontrada"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<TourActivity>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. Verificá tu red."));
                    }
                });

        return result;
    }
}
