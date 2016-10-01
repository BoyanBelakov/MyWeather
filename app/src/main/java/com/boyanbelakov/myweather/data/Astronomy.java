package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Astronomy implements JSON{
    private static final String JSON_SUNRISE = "sunrise";
    private static final String JSON_SUNSET = "sunset";

    private final Date mSunrise;
    private final Date mSunset;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("h:mm a", Locale.US);

    public Astronomy(JSONObject jsonObject) throws ParseException {
        String sunrise = jsonObject.optString(JSON_SUNRISE, null);
        mSunrise = (sunrise == null) ? null : mTimeFormat.parse(sunrise);

        String sunset = jsonObject.optString(JSON_SUNSET, null);
        mSunset = (sunset == null) ? null : mTimeFormat.parse(sunset);
    }

    public Date getSunrise() {
        return mSunrise;
    }

    public Date getSunset() {
        return mSunset;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (mSunrise != null) {
            jsonObject.put(JSON_SUNRISE, mTimeFormat.format(mSunrise));
        }
        if (mSunset != null) {
            jsonObject.put(JSON_SUNSET, mTimeFormat.format(mSunset));
        }

        return jsonObject;
    }
}
