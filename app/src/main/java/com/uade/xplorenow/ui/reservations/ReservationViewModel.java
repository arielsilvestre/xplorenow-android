package com.uade.xplorenow.ui.reservations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.data.repository.ReservationRepository;
import com.uade.xplorenow.util.Resource;

import java.util.List;

public class ReservationViewModel extends ViewModel {

    private final ReservationRepository repository;

    public ReservationViewModel() {
        repository = ReservationRepository.getInstance();
    }

    public LiveData<Resource<List<Reservation>>> getMyReservations() {
        return repository.getMyReservations();
    }

    public LiveData<Resource<Reservation>> createReservation(String activityId, String date, int people) {
        return repository.createReservation(activityId, date, people);
    }
}
