package com.uade.xplorenow.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.data.repository.ActivityRepository;
import com.uade.xplorenow.util.Resource;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class ActivityViewModel extends ViewModel {

    private final ActivityRepository repository;

    @Inject
    public ActivityViewModel(ActivityRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<TourActivity>>> getActivities() {
        return repository.getActivities();
    }

    public LiveData<Resource<List<TourActivity>>> getActivitiesFiltered(String category) {
        return repository.getActivities(category);
    }

    public LiveData<Resource<TourActivity>> getActivityById(String id) {
        return repository.getActivityById(id);
    }
}
