package com.uade.xplorenow.ui.destinations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.model.Destination;
import com.uade.xplorenow.data.repository.DestinationRepository;
import com.uade.xplorenow.util.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DestinationViewModel extends ViewModel {

    private final DestinationRepository repository;

    @Inject
    public DestinationViewModel(DestinationRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Destination>>> getDestinations() {
        return repository.getDestinations();
    }

    public LiveData<Resource<Destination>> getDestinationById(String id) {
        return repository.getDestinationById(id);
    }
}
