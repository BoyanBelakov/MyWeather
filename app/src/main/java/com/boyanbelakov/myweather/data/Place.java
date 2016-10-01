package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Place implements JSON {
    private static final String JSON_NAME = "name";
    private static final String JSON_COUNTRY = "country";
    private static final String JSON_WOEID = "woeid";

    private final String mName;
    private final Country mCountry;
    private final int mID;

    public Place(JSONObject jsonObject) {
        mName = jsonObject.optString(JSON_NAME);

        JSONObject countryObj = jsonObject.optJSONObject(JSON_COUNTRY);
        mCountry = (countryObj == null) ? null : new Country(countryObj);

        mID = jsonObject.optInt(JSON_WOEID, Integer.MIN_VALUE);
    }

    public Country getCountry() {
        return mCountry;
    }

    public int getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (mName != null){
            jsonObject.put(JSON_NAME, mName);
        }
        if (mCountry != null){
            jsonObject.put(JSON_COUNTRY, mCountry.toJSON());
        }
        if (mID != Integer.MIN_VALUE){
            jsonObject.put(JSON_WOEID, mID);
        }

        return jsonObject;
    }


}
