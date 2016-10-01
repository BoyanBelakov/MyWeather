package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Atmosphere implements JSON {
    private static final String JSON_HUMIDITY = "humidity";
    private static final String JSON_PRESSURE = "pressure";
    private static final String JSON_VISIBILITY = "visibility";

    private final int mHumidity;
    private final float mPressure;
    private final float mVisibility;

    public Atmosphere(JSONObject jsonObject) {
        int humidity = jsonObject.optInt(JSON_HUMIDITY, Integer.MIN_VALUE);
        mHumidity = (humidity == Integer.MIN_VALUE) ? Integer.MIN_VALUE : humidity;

        double pressure = jsonObject.optDouble(JSON_PRESSURE, Double.NaN);
        mPressure = Double.isNaN(pressure) ? Float.NaN : (float) pressure;

        double visibility = jsonObject.optDouble(JSON_VISIBILITY, Double.NaN);
        mVisibility = Double.isNaN(visibility) ? Float.NaN : (float) visibility;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public float getPressure() {
        return mPressure;
    }

    public float getVisibility() {
        return mVisibility;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (!Float.isNaN(mPressure)) {
            jsonObject.put(JSON_PRESSURE, mPressure);
        }
        if (!Float.isNaN(mVisibility)) {
            jsonObject.put(JSON_VISIBILITY, mVisibility);
        }
        if (mHumidity != Integer.MIN_VALUE) {
            jsonObject.put(JSON_HUMIDITY, mHumidity);
        }


        return jsonObject;
    }
}
