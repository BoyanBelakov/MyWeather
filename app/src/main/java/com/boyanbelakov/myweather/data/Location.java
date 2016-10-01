package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Location implements JSON{
    private static final String JSON_CITY = "city";
    private static final String JSON_COUNTRY = "country";

    private final String mCity;
    private final String mCountry;

    public Location(JSONObject jsonObject) {
        mCity = jsonObject.optString(JSON_CITY, null);
        mCountry = jsonObject.optString(JSON_COUNTRY, null);
    }

    public String getCity() {
        return mCity;
    }

    public String getCountry() {
        return mCountry;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (mCity != null) {
            jsonObject.put(JSON_CITY, mCity);
        }
        if (mCountry != null) {
            jsonObject.put(JSON_COUNTRY, mCountry);
        }

        return jsonObject;
    }
}
