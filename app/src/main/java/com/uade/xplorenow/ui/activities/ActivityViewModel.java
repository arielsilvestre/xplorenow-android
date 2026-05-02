package com.uade.xplorenow.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.model.Review;
import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.data.repository.ActivityRepository;
import com.uade.xplorenow.data.repository.ReviewRepository;
import com.uade.xplorenow.util.Resource;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class ActivityViewModel extends ViewModel {

    private final ActivityRepository repository;
    private final ReviewRepository reviewRepository;

    @Inject
    public ActivityViewModel(ActivityRepository repository, ReviewRepository reviewRepository) {
        this.repository = repository;
        this.reviewRepository = reviewRepository;
    }

    public LiveData<Resource<List<TourActivity>>> getActivities() {
        return repository.getActivities();
    }

    public LiveData<Resource<List<TourActivity>>> getActivitiesFiltered(String category) {
        return repository.getActivities(category);
    }

    public LiveData<Resource<List<TourActivity>>> getActivitiesByCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) return repository.getActivities();
        return repository.getActivitiesByCategories(categories);
    }

    public LiveData<Resource<TourActivity>> getActivityById(String id) {
        return repository.getActivityById(id);
    }

    public LiveData<Resource<Review>> createReview(String activityId, int stars, String comment) {
        return reviewRepository.createReview(activityId, stars, comment);
    }
}
