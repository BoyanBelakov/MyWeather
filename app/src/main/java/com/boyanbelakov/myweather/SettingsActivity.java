package com.boyanbelakov.myweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.boyanbelakov.myweather.util.Units;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = new SettingsFragment();
            fm.beginTransaction().
                    add(R.id.fragment_container, fragment).
                    commit();
        }
    }

    public static long getUpdateInterval(Context c){
        String key = c.getString(R.string.pref_key_update_interval);
        String defValue = c.getString(R.string.def_update_interval);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        String value = prefs.getString(key, defValue);

        return Long.valueOf(value);
    }

    public static Units.Temperature getTemperatureUnit(Context c) {
        String key = c.getString(R.string.pref_key_temperature_unit_c);
        boolean defValue = c.getResources().getBoolean(R.bool.def_temperature_unit_c);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        boolean value = prefs.getBoolean(key, defValue);

        return value ? Units.Temperature.c : Units.Temperature.f;
    }

    public static Units.Speed getSpeedUnit(Context c) {
        String key = c.getString(R.string.pref_key_temperature_unit_c);
        boolean defValue = c.getResources().getBoolean(R.bool.def_temperature_unit_c);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        boolean value = prefs.getBoolean(key, defValue);

        return value ? Units.Speed.kph : Units.Speed.mph;
    }

    public static Units.Pressure getPressureUnit(Context c) {
        String key = c.getString(R.string.pref_key_temperature_unit_c);
        boolean defValue = c.getResources().getBoolean(R.bool.def_temperature_unit_c);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        boolean value = prefs.getBoolean(key, defValue);

        return value ? Units.Pressure.mb : Units.Pressure.in_;
    }

    public static Units.Distance getDistanceUnit(Context c) {
        String key = c.getString(R.string.pref_key_temperature_unit_c);
        boolean defValue = c.getResources().getBoolean(R.bool.def_temperature_unit_c);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        boolean value = prefs.getBoolean(key, defValue);

        return value ? Units.Distance.km : Units.Distance.mi;
    }

    public static int getCityID(Context c) {
        String key = c.getString(R.string.pref_key_city_id);
        int defValue = c.getResources().getInteger(R.integer.def_city_id);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getInt(key, defValue);
    }

    public static void setCityID(Context c, int cityID) {
        String key = c.getString(R.string.pref_key_city_id);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        prefs.edit().putInt(key, cityID).apply();
    }

    public static void setCityName(Context c, String cityName) {
        String key = c.getString(R.string.pref_key_city_name);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        prefs.edit().putString(key, cityName).apply();
    }

    public static String getCityName(Context c) {
        String key = c.getString(R.string.pref_key_city_name);
        String defValue = c.getString(R.string.def_city_name);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getString(key, defValue);
    }
}
