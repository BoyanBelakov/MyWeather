package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Forecast implements JSON {
    private static final String JSON_CODE = "code";
    private static final String JSON_DAY = "day";
    private static final String JSON_HIGH = "high";
    private static final String JSON_LOW = "low";
    private static final String JSON_DATE = "date";

    private final int mCode;
    private final String mDay;
    private final int mHigh;
    private final int mLow;
    private final Date mDate;

    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);

    public Forecast(JSONObject jsonObject) throws ParseException {
        int code = jsonObject.optInt(JSON_CODE, Integer.MIN_VALUE);
        mCode = (code == Integer.MIN_VALUE) ? Integer.MIN_VALUE : code;

        mDay = jsonObject.optString(JSON_DAY, null);

        int high = jsonObject.optInt(JSON_HIGH, Integer.MIN_VALUE);
        mHigh = (high == Integer.MIN_VALUE) ? Integer.MIN_VALUE : high;

        int low = jsonObject.optInt(JSON_LOW, Integer.MIN_VALUE);
        mLow = (low == Integer.MIN_VALUE) ? Integer.MIN_VALUE : low;

        String date = jsonObject.optString(JSON_DATE, null);
        mDate = (date == null) ? null : mDateFormat.parse(date);
    }

    public int getCode() {
        return mCode;
    }

    public Date getDate() {
        return mDate;
    }

    public String getDay() {
        return mDay;
    }

    public int getHigh() {
        return mHigh;
    }

    public int getLow() {
        return mLow;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (mCode != Integer.MIN_VALUE) {
            jsonObject.put(JSON_CODE, mCode);
        }
        if (mDay != null) {
            jsonObject.put(JSON_DAY, mDay);
        }
        if (mHigh != Integer.MIN_VALUE) {
            jsonObject.put(JSON_HIGH, mHigh);
        }
        if (mLow != Integer.MIN_VALUE) {
            jsonObject.put(JSON_LOW, mLow);
        }
        if (mDate != null) {
            jsonObject.put(JSON_DATE, mDateFormat.format(mDate));
        }

        return jsonObject;
    }
}
