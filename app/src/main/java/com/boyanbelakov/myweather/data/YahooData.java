package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class YahooData implements JSON {
    private static final String JSON_QUERY = "query";

    private final Query mQuery;

    public YahooData(JSONObject jsonObject) throws ParseException, JSONException {
        JSONObject queryObj = jsonObject.optJSONObject(JSON_QUERY);
        mQuery = (queryObj == null) ? null : new Query(queryObj);
    }

    public Query getQuery() {
        return mQuery;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (mQuery != null){
            jsonObject.put(JSON_QUERY, mQuery.toJSON());
        }

        return jsonObject;
    }
}





