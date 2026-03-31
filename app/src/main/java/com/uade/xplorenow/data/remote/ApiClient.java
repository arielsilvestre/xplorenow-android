package com.uade.xplorenow.data.remote;

import com.uade.xplorenow.data.local.SessionManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://xplorenow-api-production.up.railway.app/";
    private static ApiClient instance;
    private final ApiService service;

    private ApiClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    // AuthInterceptor: agrega el JWT a todas las requests
                    Request original = chain.request();
                    SessionManager session = SessionManager.getInstance();
                    String token = (session != null) ? session.getCachedToken() : null;
                    if (token != null && !token.isEmpty()) {
                        Request authenticated = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(authenticated);
                    }
                    return chain.proceed(original);
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ApiService getService() {
        return service;
    }
}
