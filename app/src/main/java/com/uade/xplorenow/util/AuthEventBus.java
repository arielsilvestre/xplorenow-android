package com.uade.xplorenow.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class AuthEventBus {

    private final MutableLiveData<Boolean> sessionExpired = new MutableLiveData<>();

    @Inject
    public AuthEventBus() {}

    public void emitSessionExpired() {
        sessionExpired.postValue(true);
    }

    public void reset() {
        sessionExpired.postValue(null);
    }

    public LiveData<Boolean> getSessionExpired() {
        return sessionExpired;
    }
}
