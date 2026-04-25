package com.uade.xplorenow.ui.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.model.TourActivity;
import com.uade.xplorenow.data.repository.FavoriteRepository;
import com.uade.xplorenow.util.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoriteViewModel extends ViewModel {

    private final FavoriteRepository repository;

    @Inject
    public FavoriteViewModel(FavoriteRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<TourActivity>>> getMyFavorites() {
        return repository.getMyFavorites();
    }

    public LiveData<Resource<Void>> toggleFavorite(String activityId) {
        return repository.toggleFavorite(activityId);
    }
}
