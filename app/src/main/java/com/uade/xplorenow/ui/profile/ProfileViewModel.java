package com.uade.xplorenow.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.local.TokenManager;
import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.data.model.User;
import com.uade.xplorenow.data.remote.ApiService;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.UpdateUserRequest;
import com.uade.xplorenow.data.repository.ReservationRepository;
import com.uade.xplorenow.util.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>();
    private final MutableLiveData<Boolean> preferencesSaved = new MutableLiveData<>();
    private final TokenManager tokenManager;
    private final ReservationRepository reservationRepository;
    private final ApiService apiService;

    @Inject
    public ProfileViewModel(TokenManager tokenManager,
                            ReservationRepository reservationRepository,
                            ApiService apiService) {
        this.tokenManager = tokenManager;
        this.reservationRepository = reservationRepository;
        this.apiService = apiService;
        loadUser();
    }

    private void loadUser() {
        // Cargar desde token primero (instantáneo)
        String id = tokenManager.getUserId();
        if (id != null) {
            user.setValue(new User(id, tokenManager.getUserName(),
                    tokenManager.getUserEmail(), tokenManager.getUserRole()));
        }
        // Luego enriquecer con datos del servidor (incluye preferences)
        apiService.getMe().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) { /* offline: usa datos del token */ }
        });
    }

    public void updatePreferences(List<String> preferences) {
        apiService.updateMe(new UpdateUserRequest(preferences))
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                        preferencesSaved.setValue(response.isSuccessful());
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        preferencesSaved.setValue(false);
                    }
                });
    }

    public LiveData<User> getUser() { return user; }
    public LiveData<Boolean> getLoggedOut() { return loggedOut; }
    public LiveData<Boolean> getPreferencesSaved() { return preferencesSaved; }

    public LiveData<Resource<List<Reservation>>> getMyReservations() {
        return reservationRepository.getMyReservations();
    }

    public void logout() {
        tokenManager.clearAll();
        loggedOut.setValue(true);
    }
}
