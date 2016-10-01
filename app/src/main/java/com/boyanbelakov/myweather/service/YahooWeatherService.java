package com.boyanbelakov.myweather.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.boyanbelakov.myweather.R;
import com.boyanbelakov.myweather.SettingsActivity;
import com.boyanbelakov.myweather.data.InternalStorage;
import com.boyanbelakov.myweather.data.YahooData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HttpsURLConnection;

public class YahooWeatherService extends IntentService {
    public static final String ACTION_UPDATE_WEATHER = "com.boyanbelakov.myweather.action.UPDATE_WEATHER";
    public static final String ACTION_FOUND_CITIES = "com.boyanbelakov.myweather.action.FOUND_CITIES";
    public static final String ACTION_ERROR = "com.boyanbelakov.myweather.action.ERROR";

    public static final String EXTRA_YAHOO_DATA = "com.boyanbelakov.myweather.extra.YAHOO_DATA";
    public static final String EXTRA_ERR_USER_TEXT = "com.boyanbelakov.myweather.extra.ERR_USER_TEXT";
    public static final String EXTRA_ERR_TECHNICAL_TEXT = "com.boyanbelakov.myweather.extra.ERR_TECHNICAL_TEXT";

    private static final String ACTION_FETCH_WEATHER = "com.boyanbelakov.myweather.action.FETCH_WEATHER";
    private static final String ACTION_SEARCH_CITY = "com.boyanbelakov.myweather.action.SEARCH_CITY";

    private static final String EXTRA_CITY_ID = "com.boyanbelakov.myweather.extra.CITY_ID";
    private static final String EXTRA_TEXT = "com.boyanbelakov.myweather.extra.TEXT";

    private static final int READ_TIMEOUT_MILLIS = 60_000;
    private static final int CONNECT_TIMEOUT_MILLIS = 60_000;

    private static final String BASE_URL = "https://query.yahooapis.com/v1/public/yql";

    private static final AtomicBoolean sFetchRunning = new AtomicBoolean(false);
    private static final AtomicBoolean sSearchRunning = new AtomicBoolean(false);

    public YahooWeatherService() {
        super("YahooWeatherService");
    }

    public static boolean isFetchRunning() {
        return sFetchRunning.get();
    }

    public static boolean isSearchRunning() {
        return sSearchRunning.get();
    }

    public static Intent newFetchIntent(Context context) {
        Intent intent = new Intent(context, YahooWeatherService.class);
        intent.setAction(ACTION_FETCH_WEATHER);
        int cityID = SettingsActivity.getCityID(context);
        intent.putExtra(EXTRA_CITY_ID, cityID);

        return intent;
    }

    public static Intent newSearchIntent(Context context, String text) {
        Intent intent = new Intent(context, YahooWeatherService.class);
        intent.setAction(ACTION_SEARCH_CITY);
        intent.putExtra(EXTRA_TEXT, text);

        return intent;
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = newFetchIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn){
            long intervalMillis = SettingsActivity.getUpdateInterval(context);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), intervalMillis, pi);
        }else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = newFetchIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()){
            case ACTION_FETCH_WEATHER:
                fetchWeather(intent);
                break;
            case ACTION_SEARCH_CITY:
                search(intent);
                break;
        }
    }

    private void fetchWeather(Intent intent) {
        try {
            sFetchRunning.set(true);
            int woeid = intent.getIntExtra(EXTRA_CITY_ID, Integer.MIN_VALUE);
            if (woeid == Integer.MIN_VALUE){
                return;
            }

            String q = "select * from weather.forecast where woeid="+woeid+" and u='c'";
            String url = BASE_URL+"?q="+Uri.encode(q)+"&format=json";

            httpGet(url, ACTION_FETCH_WEATHER);
        }finally {
            sFetchRunning.set(false);
        }
    }

    private void search(Intent intent) {
        try {
            sSearchRunning.set(true);
            String text = intent.getStringExtra(EXTRA_TEXT);
            if (text == null){
                return;
            }

            String q = "select woeid, name, country from geo.places where text=\""+text+'\"';
            String url = BASE_URL+"?q="+Uri.encode(q)+"&format=json";

            httpGet(url, ACTION_SEARCH_CITY);
        }finally {
            sSearchRunning.set(false);
        }
    }

    private void httpGet(String urlAddress, String action) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        HttpsURLConnection conn = null;
        try {
            URL url = new URL(urlAddress);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setReadTimeout(READ_TIMEOUT_MILLIS);
            conn.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            conn.setRequestProperty("Accept-Language", "en-US");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                okResponse(is, action);
            }else {
                String userText = getString(R.string.err_user_text);
                String technicalText = getString(conn.getErrorStream());
                errorResponse(userText, technicalText);
            }
        }catch (Exception e){
            errorResponse(getString(R.string.err_user_text), "YahooWeatherService.httpGet failed url="+urlAddress+"\n"+e.toString());
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void okResponse(InputStream is, String action) throws IOException, JSONException,
            ParseException {
        if (ACTION_FETCH_WEATHER.equals(action)){
            String json = getString(is);
            YahooData yahooData = new YahooData(new JSONObject(json));

            InternalStorage.getInstance(this).save(yahooData);

            Intent i = new Intent(ACTION_UPDATE_WEATHER);
            i.putExtra(EXTRA_YAHOO_DATA, yahooData);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        } else if (ACTION_SEARCH_CITY.equals(action)){
            String json = getString(is);
            YahooData yahooData = new YahooData(new JSONObject(json));

            Intent i = new Intent(ACTION_FOUND_CITIES);
            i.putExtra(EXTRA_YAHOO_DATA, yahooData);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }
    }

    private void errorResponse(String errUserText, String errTechnicalText) {
        Intent i = new Intent(ACTION_ERROR);
        i.putExtra(EXTRA_ERR_USER_TEXT, errUserText);
        i.putExtra(EXTRA_ERR_TECHNICAL_TEXT, errTechnicalText);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }

    @NonNull
    private String getString(InputStream is) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return stringBuilder.toString();
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;

        return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    }
}
