package com.uade.xplorenow.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.uade.xplorenow.data.local.db.AppDatabase;
import com.uade.xplorenow.data.local.db.entity.ReservationEntity;
import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.data.remote.ApiService;
import com.uade.xplorenow.data.remote.dto.ApiResponse;
import com.uade.xplorenow.data.remote.dto.CreateReservationRequest;
import com.uade.xplorenow.util.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ReservationRepository {

    private final ApiService apiService;
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Inject
    public ReservationRepository(ApiService apiService, AppDatabase db) {
        this.apiService = apiService;
        this.db = db;
    }

    public LiveData<Resource<List<Reservation>>> getMyReservations() {
        MutableLiveData<Resource<List<Reservation>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getMyReservations()
                .enqueue(new Callback<ApiResponse<List<Reservation>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Reservation>>> call,
                                           Response<ApiResponse<List<Reservation>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Reservation> reservations = response.body().getData();
                            // Guardar en Room en background (en transacción para consistencia)
                            executor.execute(() -> db.runInTransaction(() -> {
                                db.reservationDao().deleteAll();
                                db.reservationDao().insertAll(toEntities(reservations));
                            }));
                            result.setValue(Resource.success(reservations));
                        } else {
                            // API fallida: intentar cargar desde Room
                            loadFromRoom(result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Reservation>>> call, Throwable t) {
                        // Sin conexión: cargar desde Room
                        loadFromRoom(result);
                    }
                });

        return result;
    }

    public LiveData<Resource<List<Reservation>>> getReservationHistory() {
        MutableLiveData<Resource<List<Reservation>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        apiService.getReservationHistory()
                .enqueue(new Callback<ApiResponse<List<Reservation>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Reservation>>> call,
                                           Response<ApiResponse<List<Reservation>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                        } else {
                            result.setValue(Resource.error("Error al cargar historial"));
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Reservation>>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión."));
                    }
                });
        return result;
    }

    public LiveData<Resource<Reservation>> cancelReservation(String reservationId) {
        MutableLiveData<Resource<Reservation>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        apiService.cancelReservation(reservationId)
                .enqueue(new Callback<ApiResponse<Reservation>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Reservation>> call,
                                           Response<ApiResponse<Reservation>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Reservation reservation = response.body().getData();
                            executor.execute(() -> {
                                ReservationEntity entity = toEntity(reservation);
                                if (entity != null)
                                    db.reservationDao().insertAll(Collections.singletonList(entity));
                            });
                            result.setValue(Resource.success(reservation));
                        } else {
                            result.setValue(Resource.error("Error al cancelar la reserva"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Reservation>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. No se pudo cancelar."));
                    }
                });
        return result;
    }

    public LiveData<Resource<Reservation>> createReservation(String activityId, String date, int people) {
        MutableLiveData<Resource<Reservation>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        CreateReservationRequest request = new CreateReservationRequest(activityId, date, people);

        apiService.createReservation(request)
                .enqueue(new Callback<ApiResponse<Reservation>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Reservation>> call,
                                           Response<ApiResponse<Reservation>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Reservation reservation = response.body().getData();
                            // Persistir la nueva reserva en Room en background
                            executor.execute(() ->
                                    db.reservationDao().insertAll(
                                            Collections.singletonList(toEntity(reservation))
                                    )
                            );
                            result.setValue(Resource.success(reservation));
                        } else {
                            result.setValue(Resource.error("Error al crear la reserva"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Reservation>> call, Throwable t) {
                        result.setValue(Resource.error("Sin conexión. No se pudo crear la reserva."));
                    }
                });

        return result;
    }

    // --- Helpers: mappers ---

    private void loadFromRoom(MutableLiveData<Resource<List<Reservation>>> result) {
        executor.execute(() -> {
            List<ReservationEntity> cached = db.reservationDao().getAll();
            List<Reservation> offline = fromEntities(cached);
            mainHandler.post(() -> {
                if (offline.isEmpty()) {
                    result.setValue(Resource.error("Sin conexión y sin datos guardados."));
                } else {
                    result.setValue(Resource.success(offline));
                }
            });
        });
    }

    private ReservationEntity toEntity(Reservation r) {
        if (r.getId() == null) return null;
        ReservationEntity entity = new ReservationEntity();
        entity.id = r.getId();
        entity.activityId = r.getActivityId();
        entity.activityName = (r.getActivity() != null) ? r.getActivity().getName() : null;
        entity.date = r.getDate();
        entity.people = r.getPeople();
        entity.status = r.getStatus();
        entity.savedAt = System.currentTimeMillis();
        return entity;
    }

    private List<ReservationEntity> toEntities(List<Reservation> reservations) {
        List<ReservationEntity> entities = new ArrayList<>();
        if (reservations == null) return entities;
        for (Reservation r : reservations) {
            ReservationEntity entity = toEntity(r);
            if (entity != null) entities.add(entity);
        }
        return entities;
    }

    private Reservation fromEntity(ReservationEntity entity) {
        return new Reservation(
                entity.id,
                entity.activityId,
                entity.date,
                entity.people,
                entity.status
        );
    }

    private List<Reservation> fromEntities(List<ReservationEntity> entities) {
        List<Reservation> reservations = new ArrayList<>();
        if (entities == null) return reservations;
        for (ReservationEntity e : entities) {
            reservations.add(fromEntity(e));
        }
        return reservations;
    }
}
