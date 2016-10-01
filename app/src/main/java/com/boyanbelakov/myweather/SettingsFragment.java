package com.boyanbelakov.myweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.boyanbelakov.myweather.service.YahooWeatherService;

public class SettingsFragment extends PreferenceFragmentCompat {

    private final OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            String pref_key_update_interval = getString(R.string.pref_key_update_interval);
            if (key.equals(pref_key_update_interval)) {
                updateIntervalPrefSetSummary(prefs);

                Context c = getActivity();
                if (YahooWeatherService.isServiceAlarmOn(c)){
                    YahooWeatherService.setServiceAlarm(c, false);
                }
                YahooWeatherService.setServiceAlarm(c, true);
            }
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        cityNamePrefSetSummary(prefs);
        updateIntervalPrefSetSummary(prefs);

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
    }

    private void updateIntervalPrefSetSummary(SharedPreferences prefs) {
        String key = getString(R.string.pref_key_update_interval);
        String defValue = getString(R.string.def_update_interval);
        String value = prefs.getString(key, defValue);

        String[] values = getResources().getStringArray(R.array.update_intervals_values);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                String[] ui = getResources().getStringArray(R.array.update_intervals_ui);

                Preference pref = findPreference(key);
                pref.setSummary(ui[i]);
                break;
            }
        }
    }

    private void cityNamePrefSetSummary(SharedPreferences prefs) {
        String key = getString(R.string.pref_key_city_name);
        String defValue = getString(R.string.def_city_name);
        String value = prefs.getString(key, defValue);

        Preference pref = findPreference(key);
        pref.setSummary(value);
    }
}
