package com.uade.xplorenow.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class TokenManager {

    private static final String PREFS_NAME = "xplorenow_prefs";
    private static final String ENCRYPTED_PREFS_NAME = "xplorenow_secure_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_ENCRYPTED_TOKEN = "encrypted_jwt_token";

    private final SharedPreferences prefs;
    private final Context context;

    @Inject
    public TokenManager(@ApplicationContext Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean hasToken() {
        return getToken() != null;
    }

    public void saveUser(String id, String name, String email, String role) {
        prefs.edit()
                .putString(KEY_USER_ID, id != null ? id : "")
                .putString(KEY_USER_NAME, name != null ? name : "")
                .putString(KEY_USER_EMAIL, email != null ? email : "")
                .putString(KEY_USER_ROLE, role != null ? role : "user")
                .apply();
    }

    public String getUserId()    { return prefs.getString(KEY_USER_ID, null); }
    public String getUserName()  { return prefs.getString(KEY_USER_NAME, null); }
    public String getUserEmail() { return prefs.getString(KEY_USER_EMAIL, null); }
    public String getUserRole()  { return prefs.getString(KEY_USER_ROLE, "user"); }

    public void setBiometricEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply();
    }

    public boolean isBiometricEnabled() {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }

    public void saveEncryptedToken(String token) {
        try {
            SharedPreferences ep = getEncryptedPrefs();
            if (token != null) {
                ep.edit().putString(KEY_ENCRYPTED_TOKEN, token).apply();
            } else {
                ep.edit().remove(KEY_ENCRYPTED_TOKEN).apply();
            }
        } catch (GeneralSecurityException | IOException e) {
            android.util.Log.e("XploreNow", "Error al inicializar EncryptedSharedPreferences", e);
            // No guardar el token sin encriptar — es un dato sensible
        }
    }

    public String getEncryptedToken() {
        try {
            return getEncryptedPrefs().getString(KEY_ENCRYPTED_TOKEN, null);
        } catch (GeneralSecurityException | IOException e) {
            android.util.Log.e("XploreNow", "Error al leer EncryptedSharedPreferences", e);
            return null;
        }
    }

    public void clearAll() {
        prefs.edit().clear().apply();
        try {
            getEncryptedPrefs().edit().clear().apply();
        } catch (GeneralSecurityException | IOException e) {
            // ignorar
        }
    }

    private SharedPreferences getEncryptedPrefs() throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();
        return EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }
}
