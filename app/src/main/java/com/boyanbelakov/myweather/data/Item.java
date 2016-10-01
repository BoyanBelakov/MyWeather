package com.boyanbelakov.myweather.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class Item implements JSON{
    private static final String JSON_CONDITION = "condition";
    private static final String JSON_FORECAST = "forecast";

    private final Condition mCondition;
    private final Forecast[] mForecast;

    public Item(JSONObject jsonObject) throws JSONException, ParseException {
        JSONObject conditionObj = jsonObject.optJSONObject(JSON_CONDITION);
        mCondition = (conditionObj == null) ? null : new Condition(conditionObj);

        JSONArray forecastArr = jsonObject.optJSONArray(JSON_FORECAST);
        if (forecastArr == null || forecastArr.length() == 0){
            mForecast = null;
        }else {
            mForecast = new Forecast[forecastArr.length()];
            for (int i = 0; i < mForecast.length; i++) {
                mForecast[i] = new Forecast(forecastArr.getJSONObject(i));
            }
        }
    }

    public Condition getCondition() {
        return mCondition;
    }

    public Forecast[] getForecast() {
        return mForecast;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (mCondition != null){
            jsonObject.put(JSON_CONDITION, mCondition.toJSON());
        }
        if (mForecast != null){
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < mForecast.length; i++) {
                jsonArray.put(i, mForecast[i].toJSON());
            }
            jsonObject.put(JSON_FORECAST, jsonArray);
        }

        return jsonObject;
    }

}
