package in.esmartsolution.shree.pro.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.Set;

import in.esmartsolution.shree.pro.model.UserData;


public class Preferences {
    private static final String LOGGED_IN_USER = "LOGGED_IN_USER";
    private static final String TOKEN = "TOKEN";
    private static final String LAT = "LAT";
    private static final String LNG = "LNG";
    private static final String CYCLE_STATION_ID = "CYCLE_STATION_ID";

    private Context context;
    Set<String> strings;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Preferences(Context context) {
        this.context = context;
    }

    protected SharedPreferences getSharedPreferences(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String getString(String key, String def) {
        SharedPreferences prefs = getSharedPreferences(key);
        return prefs.getString(key, def);
    }

    public void setString(String key, String val) {
        SharedPreferences prefs = getSharedPreferences(key);
        Editor e = prefs.edit();
        e.putString(key, val);
        e.apply();
    }

    private long getLong(String key, long def) {
        SharedPreferences prefs = getSharedPreferences(key);
        return prefs.getLong(key, def);
    }

    public void setLong(String key, long val) {
        SharedPreferences prefs = getSharedPreferences(key);
        Editor e = prefs.edit();
        e.putLong(key, val);
        e.apply();
    }

    private boolean getBoolean(String key, boolean def) {
        SharedPreferences prefs = getSharedPreferences(key);
        boolean b = prefs.getBoolean(key, def);
        return b;
    }

    private void setBoolean(String key, boolean val) {
        SharedPreferences prefs = getSharedPreferences(key);
        Editor e = prefs.edit();
        e.putBoolean(key, val);
        e.apply();
    }

    public boolean isLoggedInUser() {
        String json = getString(LOGGED_IN_USER, null);
        return json != null && !TextUtils.isEmpty(json);
    }

    public void logOutUser() {
        SharedPreferences prefs = getSharedPreferences(LOGGED_IN_USER);
        Editor e = prefs.edit();
        e.clear();
        e.apply();
    }

    public UserData getLoggedInUser() {
        String json = getString(LOGGED_IN_USER, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return new Gson().fromJson(json, UserData.class);
    }

    public void setLoggedInUser(UserData user) {
        setString(LOGGED_IN_USER, new Gson().toJson(user));
    }

    private Set<String> getStringSet(String key, Set<String> def) {
        SharedPreferences prefs = getSharedPreferences(key);
        return prefs.getStringSet(key, def);
    }

    public void setStringSet(String key, Set<String> val) {
        SharedPreferences prefs = getSharedPreferences(key);
        Editor e = prefs.edit();
        e.putStringSet(key, val);
        e.apply();
    }

    public String getToken() {
        return getString(TOKEN, null);
    }

    public void setToken(String token) {
        setString(TOKEN, token);
    }

    public String getLat() {
        return getString(LAT, null);
    }

    public void setLat(String lat) {
        setString(LAT, lat);
    }

    public String getLng() {
        return getString(LNG, null);
    }

    public void setLng(String lng) {
        setString(LNG, lng);
    }

    public String getCycleStationId() {
        return getString(CYCLE_STATION_ID, null);
    }

    public void setCycleStationId(String lng) {
        setString(CYCLE_STATION_ID, lng);
    }
}
