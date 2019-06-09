package com.ottice.ottice.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * TODO: Add a class header comment!
 */
public class PrefrencesClass {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public static void savePreference(Context context, String key, String value)
    {
        sharedPreferences = context.getSharedPreferences(Common.KEY_PREFERENCES_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String getPreferenceValue(Context context, String key){
        sharedPreferences = context.getSharedPreferences(Common.KEY_PREFERENCES_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }


    @SuppressLint("CommitPrefEdits")
    public static void savePreferenceBoolean(Context context, String key, boolean value)
    {
        sharedPreferences = context.getSharedPreferences(Common.KEY_PREFERENCES_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getPreferenceBoolean(Context context, String key)
    {
        sharedPreferences = context.getSharedPreferences(Common.KEY_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,false);
    }

    @SuppressLint("CommitPrefEdits")
    public static void savePreferenceInt(Context context, String key, int value)
    {
        sharedPreferences = context.getSharedPreferences(Common.KEY_PREFERENCES_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getPreferenceInt(Context context, String key)
    {
        sharedPreferences = context.getSharedPreferences(Common.KEY_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key,0);
    }
}
