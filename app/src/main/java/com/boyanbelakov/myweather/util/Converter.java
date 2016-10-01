package com.boyanbelakov.myweather.util;

import android.content.Context;

import com.boyanbelakov.myweather.R;

class Converter {
    private Converter() {}

    public static int convertCelsiusToFahrenheit(int tempC){
        return (tempC * 9) / 5 + 32;
    }

    public static float convertKilometersToMiles(float km){
        return km / 1.609344f;
    }

    public static float convertMillibarsToInches(float mb){
        return 0.0295300f * mb;
    }

    public static String convertDegreeToDirection(Context context, float degree){
        if ((degree < 0.0f) || (degree > 360.0f)) {
            return "";
        }

        String direction;
        if (degree <= 22.5f) {
            direction = context.getString(R.string.direction_N);
        } else if (degree <= 67.5f) {
            direction = context.getString(R.string.direction_NE);
        } else if (degree <= 112.5f) {
            direction = context.getString(R.string.direction_E);
        } else if (degree <= 157.5f) {
            direction = context.getString(R.string.direction_SE);
        } else if (degree <= 202.5f) {
            direction = context.getString(R.string.direction_S);
        } else if (degree <= 247.5f) {
            direction = context.getString(R.string.direction_SW);
        } else if (degree <= 292.5f) {
            direction = context.getString(R.string.direction_W);
        } else if (degree <= 337.5f) {
            direction = context.getString(R.string.direction_NW);
        } else {
            direction = context.getString(R.string.direction_N);
        }

        return direction;
    }
}
