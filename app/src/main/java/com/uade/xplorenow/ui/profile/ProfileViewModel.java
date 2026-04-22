package com.uade.xplorenow.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uade.xplorenow.data.local.TokenManager;
import com.uade.xplorenow.data.model.User;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>();
    private final TokenManager tokenManager;

    @Inject
    public ProfileViewModel(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        loadUser();
    }

    private void loadUser() {
        String id = tokenManager.getUserId();
        if (id != null) {
            user.setValue(new User(id, tokenManager.getUserName(),
                    tokenManager.getUserEmail(), tokenManager.getUserRole()));
        }
    }

    public LiveData<User> getUser() { return user; }
    public LiveData<Boolean> getLoggedOut() { return loggedOut; }

    public void logout() {
        tokenManager.clearAll();
        loggedOut.setValue(true);
    }
}
