package com.boyanbelakov.myweather.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class Results implements JSON {
    private static final String JSON_CHANNEL = "channel";
    private static final String JSON_PLACE = "place";

    private final Channel mChannel;
    private final Place[] mPlace;

    public Results(JSONObject jsonObject) throws ParseException, JSONException {
        JSONObject channelObj = jsonObject.optJSONObject(JSON_CHANNEL);
        mChannel = (channelObj == null) ? null : new Channel(channelObj);

        JSONObject placeObj = jsonObject.optJSONObject(JSON_PLACE);
        if (placeObj != null){
            mPlace = new Place[1];
            mPlace[0] = new Place(placeObj);
        } else {
            JSONArray arr = jsonObject.optJSONArray(JSON_PLACE);
            if (arr != null){
                int len = arr.length();
                mPlace = new Place[len];
                for (int i = 0; i < len; i++) {
                    JSONObject o = arr.getJSONObject(i);
                    mPlace[i] = new Place(o);
                }
            }else {
                mPlace = null;
            }
        }
    }

    public Channel getChannel() {
        return mChannel;
    }

    public Place[] getPlace() {
        return mPlace;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (mChannel != null){
            jsonObject.put(JSON_CHANNEL, mChannel.toJSON());
        }
        if (mPlace != null){
            JSONArray arr = new JSONArray();
            for (int i = 0; i < mPlace.length; i++) {
                arr.put(i, mPlace[i].toJSON());
            }
            jsonObject.put(JSON_PLACE, arr);
        }

        return jsonObject;
    }
}
