package com.codepath.apps.restclienttemplate.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.codepath.apps.restclienttemplate.models.UserModel;
import com.google.gson.Gson;

/**
 * Created by kpirwani on 2/21/16.
 */
public class SharedPrefsHelper {
    private final static String PREFS_NAME = "TWEATER_PREFS";
    private final static String CURRENT_USER_KEY = "CURRENT_USER";

    public static void setCurrentUser(Context context, UserModel user) {
        SharedPreferences  mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString(CURRENT_USER_KEY, json);
        prefsEditor.commit();
    }

    public static UserModel getCurrentUser(Context context) {
        SharedPreferences  mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(CURRENT_USER_KEY, "");
        UserModel user = gson.fromJson(json, UserModel.class);
        return user;
    }
}
