package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Country implements JSON{
    private static final String JSON_CODE = "code";

    private final String mCode;

    public Country(JSONObject jsonObject) {
        mCode = jsonObject.optString(JSON_CODE);
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (mCode != null){
            jsonObject.put(JSON_CODE, mCode);
        }

        return jsonObject;
    }
}
