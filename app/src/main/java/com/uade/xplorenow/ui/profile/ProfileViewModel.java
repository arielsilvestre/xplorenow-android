package com.uade.xplorenow.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.local.SessionManager;
import com.uade.xplorenow.data.model.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public ProfileViewModel() {
        loadUser();
    }

    private void loadUser() {
        SessionManager session = SessionManager.getInstance();
        if (session == null) return;

        disposables.add(
            session.getUser()
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
        SessionManager session = SessionManager.getInstance();
        if (session == null) {
            loggedOut.setValue(true);
            return;
        }
        disposables.add(
            session.clearSession()
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
