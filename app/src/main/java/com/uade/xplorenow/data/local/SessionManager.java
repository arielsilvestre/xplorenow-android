package com.uade.xplorenow.data.local;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import com.uade.xplorenow.data.model.User;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * SessionManager — gestiona la sesión del usuario usando DataStore (Preferences).
 * Almacena el JWT y los datos básicos del usuario de forma persistente.
 * (Semana 5: DataStore)
 */
public class SessionManager {

    private static final String DATASTORE_NAME = "xplorenow_session";
    private static final Preferences.Key<String> KEY_TOKEN    = PreferencesKeys.stringKey("token");
    private static final Preferences.Key<String> KEY_USER_ID  = PreferencesKeys.stringKey("user_id");
    private static final Preferences.Key<String> KEY_USER_NAME  = PreferencesKeys.stringKey("user_name");
    private static final Preferences.Key<String> KEY_USER_EMAIL = PreferencesKeys.stringKey("user_email");
    private static final Preferences.Key<String> KEY_USER_ROLE  = PreferencesKeys.stringKey("user_role");

    private static SessionManager instance;
    private final RxDataStore<Preferences> dataStore;

    // Cache en memoria para el AuthInterceptor (OkHttp es síncrono)
    private String cachedToken = null;

    private SessionManager(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, DATASTORE_NAME).build();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    /** Para uso en el AuthInterceptor de OkHttp (síncrono, usa cache en memoria) */
    public static SessionManager getInstance() {
        return instance;
    }

    public String getCachedToken() {
        return cachedToken;
    }

    public boolean hasSession() {
        return cachedToken != null && !cachedToken.isEmpty();
    }

    /** Lee el token desde DataStore. El RxDataStore ya maneja el scheduler internamente. */
    public Flowable<String> getToken() {
        return dataStore.data()
                .map(prefs -> {
                    String token = prefs.get(KEY_TOKEN);
                    cachedToken = token;
                    return token != null ? token : "";
                });
    }

    /** Lee el usuario guardado. No emite si no hay sesión guardada. */
    public Flowable<User> getUser() {
        return dataStore.data()
                .flatMap(prefs -> {
                    String id = prefs.get(KEY_USER_ID);
                    if (id == null || id.isEmpty()) return Flowable.empty();
                    return Flowable.just(new User(
                            id,
                            prefs.get(KEY_USER_NAME),
                            prefs.get(KEY_USER_EMAIL),
                            prefs.get(KEY_USER_ROLE) != null ? prefs.get(KEY_USER_ROLE) : "user"
                    ));
                });
    }

    /** Guarda token y datos del usuario en DataStore y en cache. */
    public Single<Preferences> saveSession(String token, User user) {
        cachedToken = token;
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
            mutablePrefs.set(KEY_TOKEN,      token);
            mutablePrefs.set(KEY_USER_ID,    user.getId()    != null ? user.getId()    : "");
            mutablePrefs.set(KEY_USER_NAME,  user.getName()  != null ? user.getName()  : "");
            mutablePrefs.set(KEY_USER_EMAIL, user.getEmail() != null ? user.getEmail() : "");
            mutablePrefs.set(KEY_USER_ROLE,  user.getRole()  != null ? user.getRole()  : "user");
            // Cast explícito: DataStore espera Single<Preferences>, no Single<MutablePreferences>
            return Single.just((Preferences) mutablePrefs);
        });
    }

    /** Borra la sesión (logout). */
    public Single<Preferences> clearSession() {
        cachedToken = null;
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
            mutablePrefs.clear();
            return Single.just((Preferences) mutablePrefs);
        });
    }
}
