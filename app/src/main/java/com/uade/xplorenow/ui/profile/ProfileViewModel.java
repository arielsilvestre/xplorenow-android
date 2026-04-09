package com.uade.xplorenow.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.local.SessionManager;
import com.uade.xplorenow.data.model.User;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final SessionManager sessionManager;

    @Inject
    public ProfileViewModel(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        loadUser();
    }

    private void loadUser() {
        disposables.add(
            sessionManager.getUser()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    u -> user.setValue(u),
                    error -> { /* sin usuario guardado */ }
                )
        );
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Boolean> getLoggedOut() {
        return loggedOut;
    }

    public void logout() {
        disposables.add(
            sessionManager.clearSession()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    prefs -> loggedOut.setValue(true),
                    error -> loggedOut.setValue(true)
                )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
