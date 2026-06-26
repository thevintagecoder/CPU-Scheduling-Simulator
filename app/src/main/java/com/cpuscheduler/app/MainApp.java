package com.cpuscheduler.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

/** Applies the saved theme before any activity inflates. */
public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(
                ThemePref.isDark(this) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
