package com.said.oubella.so.artanddesign.preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

public final class AppPreferencesImpl implements AppPreferences {

    private static final String SHARED_PREFS_NAME = "theme";

    private static final String PREF_THEME_KEY = "theme";

    private final SharedPreferences prefs;

    public AppPreferencesImpl(Application context) {
        this.prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void toggleNightTheme() {
        prefs.edit().putBoolean(PREF_THEME_KEY, !isNight()).apply();
        applyTheme();
    }

    @Override
    public void applyTheme() {
        AppCompatDelegate.setDefaultNightMode(nightMode());
    }

    private int nightMode() {
        return isNight()
                ? AppCompatDelegate.MODE_NIGHT_YES
                : Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                : AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
    }

    private boolean isNight() {
        return prefs.getBoolean(PREF_THEME_KEY, false);
    }
}
