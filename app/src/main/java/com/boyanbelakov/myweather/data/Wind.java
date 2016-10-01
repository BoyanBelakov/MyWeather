package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Wind implements JSON{
    private static final String JSON_DIRECTION = "direction";
    private static final String JSON_SPEED = "speed";

    private final int mDirection;
    private final float mSpeed;

    public Wind(JSONObject jsonObject) {
        int direction = jsonObject.optInt(JSON_DIRECTION, Integer.MIN_VALUE);
        mDirection = (direction == Integer.MIN_VALUE) ? Integer.MIN_VALUE : direction;

        double speed = jsonObject.optDouble(JSON_SPEED, Double.NaN);
        mSpeed = Double.isNaN(speed) ? Float.NaN : (float) speed;
    }

    public int getDirection() {
        return mDirection;
    }

    public float getSpeed() {
        return mSpeed;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (mDirection != Integer.MIN_VALUE) {
            jsonObject.put(JSON_DIRECTION, mDirection);
        }
        if (!Float.isNaN(mSpeed)) {
            jsonObject.put(JSON_SPEED, mSpeed);
        }

        return jsonObject;
    }
}
