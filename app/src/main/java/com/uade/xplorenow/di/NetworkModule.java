package com.uade.xplorenow.di;

import com.uade.xplorenow.data.local.TokenManager;
import com.uade.xplorenow.data.remote.ApiService;
import com.uade.xplorenow.util.AuthEventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    private static final String BASE_URL = "https://xplorenow-api-production.up.railway.app/";

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(TokenManager tokenManager, AuthEventBus authEventBus) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    String token = tokenManager.getToken();
                    Request request = token != null
                            ? original.newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .build()
                            : original;
                    Response response = chain.proceed(request);
                    if (response.code() == 401) {
                        tokenManager.clearAll();
                        authEventBus.emitSessionExpired();
                    }
                    return response;
                })
                .addInterceptor(logging)
                .build();
    }

    @Provides
    @Singleton
    public static Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public static ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
