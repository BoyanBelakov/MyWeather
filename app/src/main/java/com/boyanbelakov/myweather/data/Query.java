package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class Query implements JSON {
    private static final String JSON_RESULTS = "results";
    private final Results mResults;

    public Query(JSONObject jsonObject) throws ParseException, JSONException {
        JSONObject resultsObj = jsonObject.optJSONObject(JSON_RESULTS);
        mResults = (resultsObj == null) ? null : new Results(resultsObj);
    }

    public Results getResults() {
        return mResults;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (mResults != null){
            jsonObject.put(JSON_RESULTS, mResults.toJSON());
        }

        return jsonObject;
    }
}
