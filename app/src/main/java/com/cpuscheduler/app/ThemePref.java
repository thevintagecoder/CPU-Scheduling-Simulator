package com.cpuscheduler.app;

import android.content.Context;

/** Persists the dark/light choice. The app defaults to dark, like the original. */
public final class ThemePref {

    private ThemePref() {}

    private static final String FILE = "theme";
    private static final String KEY_DARK = "dark";

    public static boolean isDark(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_DARK, true);
    }

    public static void setDark(Context c, boolean dark) {
        c.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_DARK, dark).apply();
    }
}
