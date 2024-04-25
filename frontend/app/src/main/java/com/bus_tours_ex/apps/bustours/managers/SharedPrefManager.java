package com.bus_tours_ex.apps.bustours.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_ADMIN = "isAdmin";
    private static final String KEY_IS_ANON = "isAnon";

    private static SharedPrefManager instance;
    private final SharedPreferences prefs;

    private SharedPrefManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void setEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null); // Return null if email hasn't been saved yet
    }

    public void setIsAdmin(boolean isAdmin) {
        prefs.edit().putBoolean(KEY_IS_ADMIN, isAdmin).apply();
    }

    public boolean getIsAdmin() {
        return prefs.getBoolean(KEY_IS_ADMIN, false); // Default to false if not set
    }

    public void setIsAnon(boolean isAnon) {
        prefs.edit().putBoolean(KEY_IS_ANON, isAnon).apply();
    }

    public boolean getIsAnon() {
        return prefs.getBoolean(KEY_IS_ANON, false); // Default to false if not set
    }
}

