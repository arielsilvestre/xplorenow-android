package com.uade.xplorenow.di;

import android.content.Context;

import com.uade.xplorenow.data.local.SessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class SessionModule {

    @Provides
    @Singleton
    public static SessionManager provideSessionManager(@ApplicationContext Context context) {
        return new SessionManager(context);
    }
}
