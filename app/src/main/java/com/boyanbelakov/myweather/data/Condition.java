package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Condition implements JSON{
    private static final String JSON_CODE = "code";
    private static final String JSON_TEMP = "temp";

    private final int mCode;
    private final int mTemp;

    public Condition(JSONObject jsonObject) {
        int code = jsonObject.optInt(JSON_CODE, Integer.MIN_VALUE);
        mCode = (code == Integer.MIN_VALUE) ? Integer.MIN_VALUE : code;

        int temp = jsonObject.optInt(JSON_TEMP, Integer.MIN_VALUE);
        mTemp = (temp == Integer.MIN_VALUE) ? Integer.MIN_VALUE : temp;
    }

    public int getCode() {
        return mCode;
    }

    public int getTemp() {
        return mTemp;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (mCode != Integer.MIN_VALUE) {
            jsonObject.put(JSON_CODE, mCode);
        }
        if (mTemp != Integer.MIN_VALUE) {
            jsonObject.put(JSON_TEMP, mTemp);
        }

        return jsonObject;
    }
}
