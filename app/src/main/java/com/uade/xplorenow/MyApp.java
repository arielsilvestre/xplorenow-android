package com.uade.xplorenow;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApp extends Application {
    // Hilt genera el componente raíz en compile time
}
