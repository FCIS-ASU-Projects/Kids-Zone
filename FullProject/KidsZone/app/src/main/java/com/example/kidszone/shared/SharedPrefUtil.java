package com.example.kidszone.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SharedPrefUtil {
    private static final String SHARED_APP_PREFERENCE_NAME = "SharedPref";
    private final String EXTRA_LAST_APP = "EXTRA_LAST_APP";
    private final SharedPreferences pref;

    public SharedPrefUtil(Context context) {
        this.pref = context.getSharedPreferences(SHARED_APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefUtil getInstance(Context context) {
        return new SharedPrefUtil(context);
    }

    public void putString(String key, String value) {
        pref.edit().putString(key, value).apply();
    }

    public void putInteger(String key, int value) {
        pref.edit().putInt(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        pref.edit().putBoolean(key, value).apply();
    }

    public String getString(String key) {
        return pref.getString(key, "");
    }

    public int getInteger(String key) {
        return pref.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        return pref.getBoolean(key, false);
    }

    public String getLastApp() {
        return getString(EXTRA_LAST_APP);
    }

    public void setLastApp(String packageName) {
        putString(EXTRA_LAST_APP, packageName);
    }

    public void clearLastApp() {pref.edit().remove(EXTRA_LAST_APP);}

    // TODO add apps to BLOCKED list
    public void createUnblockedAppsList(List<String> appList) {
        for (int i = 0; i < appList.size(); i++) {
            putString("unblocked_app_" + i, appList.get(i));
        }
        putInteger("unblockedListSize", appList.size());
    }

    // TODO get apps from BLOCKED list
    public List<String> getUnblockedAppsList() {
        int size = getInteger("unblockedListSize");
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            temp.add(getString("unblocked_app_" + i));
        }
        return temp;
    }

}