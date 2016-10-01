package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Channel implements JSON{
    private static final String JSON_LAST_BUILD_DATE = "lastBuildDate";
    private static final String JSON_LOCATION = "location";
    private static final String JSON_WIND = "wind";
    private static final String JSON_ATMOSPHERE = "atmosphere";
    private static final String JSON_ASTRONOMY = "astronomy";
    private static final String JSON_ITEM = "item";

    private final Date mLastBuildDate;
    private final Location mLocation;
    private final Wind mWind;
    private final Atmosphere mAtmosphere;
    private final Astronomy mAstronomy;
    private final Item mItem;

    private final SimpleDateFormat mDateFormat = new
            SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.US);

    public Channel(JSONObject jsonObject) throws ParseException, JSONException {
        String date = jsonObject.optString(JSON_LAST_BUILD_DATE, null);
        mLastBuildDate = (date == null) ? null : mDateFormat.parse(date);

        JSONObject locationObj = jsonObject.optJSONObject(JSON_LOCATION);
        mLocation = (locationObj == null) ? null : new Location(locationObj);

        JSONObject windObj = jsonObject.optJSONObject(JSON_WIND);
        mWind = (windObj == null) ? null : new Wind(windObj);

        JSONObject atmosphereObj = jsonObject.optJSONObject(JSON_ATMOSPHERE);
        mAtmosphere = (atmosphereObj == null) ? null : new Atmosphere(atmosphereObj);

        JSONObject astronomyObj = jsonObject.optJSONObject(JSON_ASTRONOMY);
        mAstronomy = (astronomyObj == null) ? null : new Astronomy(astronomyObj);

        JSONObject itemObj = jsonObject.optJSONObject(JSON_ITEM);
        mItem = (itemObj == null) ? null : new Item(itemObj);
    }

    public Date getLastBuildDate() {
        return mLastBuildDate;
    }

    public Astronomy getAstronomy() {
        return mAstronomy;
    }

    public Atmosphere getAtmosphere() {
        return mAtmosphere;
    }

    public Item getItem() {
        return mItem;
    }

    public Location getLocation() {
        return mLocation;
    }

    public Wind getWind() {
        return mWind;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (mLastBuildDate != null) {
            jsonObject.put(JSON_LAST_BUILD_DATE, mDateFormat.format(mLastBuildDate));
        }
        if (mLocation != null){
            jsonObject.put(JSON_LOCATION, mLocation.toJSON());
        }
        if (mWind != null){
            jsonObject.put(JSON_WIND, mWind.toJSON());
        }
        if (mAtmosphere != null){
            jsonObject.put(JSON_ATMOSPHERE, mAtmosphere.toJSON());
        }
        if (mAstronomy != null){
            jsonObject.put(JSON_ASTRONOMY, mAstronomy.toJSON());
        }
        if (mItem != null){
            jsonObject.put(JSON_ITEM, mItem.toJSON());
        }

        return jsonObject;
    }
}
