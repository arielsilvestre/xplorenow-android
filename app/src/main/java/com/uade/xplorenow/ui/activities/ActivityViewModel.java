package com.uade.xplorenow.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.data.repository.ActivityRepository;
import com.uade.xplorenow.util.Resource;

import java.util.List;

public class ActivityViewModel extends ViewModel {

    private final ActivityRepository repository;

    public ActivityViewModel() {
        repository = ActivityRepository.getInstance();
    }

    public LiveData<Resource<List<TourActivity>>> getActivities() {
        return repository.getActivities();
    }

    public LiveData<Resource<TourActivity>> getActivityById(String id) {
        return repository.getActivityById(id);
    }
}
