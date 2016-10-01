package com.boyanbelakov.myweather.data;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;

public class InternalStorage {
    private static final String FILE_NAME_WEATHER = "weather.json";
    private static final String CHARSET_NAME = "UTF-8";

    private static InternalStorage sInstance;
    private final Context mContext;

    private InternalStorage(Context context) {
        mContext = context;
    }

    public static InternalStorage getInstance(Context context) {
        if (sInstance == null){
            sInstance = new InternalStorage(context.getApplicationContext());
        }
        return sInstance;
    }

    public synchronized void save(YahooData weatherData) throws IOException, JSONException {
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = mContext.openFileOutput(FILE_NAME_WEATHER, 0);
            writer = new BufferedWriter(new OutputStreamWriter(fos, CHARSET_NAME));
            writer.write(weatherData.toJSON().toString());
        }finally {
            if (writer != null){
                writer.close();
            }
        }
    }

    public synchronized YahooData load() throws IOException, JSONException, ParseException {
        BufferedReader reader = null;
        try {
            FileInputStream fis = mContext.openFileInput(FILE_NAME_WEATHER);
            reader = new BufferedReader(new InputStreamReader(fis, CHARSET_NAME));

            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                jsonBuilder.append(line).append('\n');
            }

            String json = jsonBuilder.toString();
            return new YahooData(new JSONObject(json));
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            if (reader != null){
                reader.close();
            }
        }
    }
}
