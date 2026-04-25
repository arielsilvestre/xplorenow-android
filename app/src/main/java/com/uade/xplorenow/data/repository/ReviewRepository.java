package com.uade.xplorenow.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.uade.xplorenow.data.model.Review;
import com.uade.xplorenow.data.remote.ApiService;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.CreateReviewRequest;
import com.uade.xplorenow.util.Resource;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ReviewRepository {

    private final ApiService apiService;

    @Inject
    public ReviewRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<Review>> createReview(String activityId, int stars, String comment) {
        MutableLiveData<Resource<Review>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        apiService.createReview(new CreateReviewRequest(activityId, stars, comment))
                .enqueue(new Callback<ApiResponse<Review>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Review>> call,
                                           Response<ApiResponse<Review>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("No se pudo enviar la calificación"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Review>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. No se pudo calificar."));
                    }
                });
        return result;
    }
}
