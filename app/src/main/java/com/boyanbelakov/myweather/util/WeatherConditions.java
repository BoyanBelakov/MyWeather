package com.boyanbelakov.myweather.util;

import android.content.Context;

import com.boyanbelakov.myweather.R;

/**
 * Maps the Yahoo Weather codes to the weather icons font characters
 * https://developer.yahoo.com/weather/documentation.html#codes
 */
public enum WeatherConditions {
    TORNADO(                 0, "\uf056"),
    TROPICAL_STORM(          1, "\uf01d"),
    HURRICANE(               2, "\uf073"),
    SEVERE_THUNDERSTORMS(    3, "\uf01e"),
    THUNDERSTORMS(           4, "\uf01e"),
    MIXED_RAIN_AND_SNOW(     5, "\uf017"),
    MIXED_RAIN_AND_SLEET(    6, "\uf0b5"),
    MIXED_SNOW_AND_SLEET(    7, "\uf0b5"),
    FREEZING_DRIZZLE(        8, "\uF01C"),
    DRIZZLE(                 9, "\uf01c"),
    FREEZING_RAIN(           10, "\uf019"),
    SHOWERS1(                11, "\uf01a"),
    SHOWERS2(                12,"\uf01a"),
    SNOW_FLURRIES(           13, "\uf01b"),
    LIGHT_SNOW_SHOWERS(      14,"\uf01b"),
    BLOWING_SNOW(            15, "\uf064"),
    SNOW(                    16, "\uf01b"),
    HAIL(                    17, "\uf015"),
    SLEET(                   18, "\uf0b5"),
    DUST(                    19, "\uf063"),
    FOGGY(                   20, "\uf014"),
    HAZE(                    21, "\uf0b6"),
    SMOKY(                   22, "\uf062"),
    BLUSTERY(                23, "\uf050"),
    WINDY(                   24, "\uf0c5"),
    COLD(                    25, "\uf076"),
    CLOUDY(                  26, "\uf013"),
    MOSTLY_CLOUDY_NIGHT(     27, "\uf086"),
    MOSTLY_CLOUDY_DAY(       28, "\uf002"),
    PARTLY_CLOUDY_NIGHT(     29, "\uf083"),
    PARTLY_CLOUDY_DAY(       30, "\uf00c"),
    CLEAR_NIGHT(             31, "\uf02e"),
    SUNNY(                   32, "\uf00d"),
    FAIR_NIGHT(              33, "\uf02e"),
    FAIR_DAY(                34, "\uf00d"),
    MIXED_RAIN_AND_HAIL(     35, "\uf015"),
    HOT(                     36, "\uf072"),
    ISOLATED_THUNDERSTORMS(  37, "\uf01e"),
    SCATTERED_THUNDERSTORMS1(38, "\uf01e"),
    SCATTERED_THUNDERSTORMS2(39, "\uf01e"),
    SCATTERED_SHOWERS(       40, "\uf01a"),
    HEAVY_SNOW1(             41, "\uf01b"),
    SCATTERED_SNOW_SHOWERS(  42, "\uf01b"),
    HEAVY_SNOW2(             43, "\uf01b"),
    PARTLY_CLOUDY(           44, "\uf013"),
    THUNDERSHOWERS(          45, "\uf01e"),
    SNOW_SHOWERS(            46,"\uf01b"),
    ISOLATED_THUNDERSHOWERS( 47, "\uf01d"),
    NOT_AVAILABLE(           3200, "\uf07b");

    private final int mCode;
    private final String mIcon;

    WeatherConditions(int code, String icon) {
        mCode = code;
        mIcon = icon;
    }


    public String getIcon() {
        return mIcon;
    }

    public static WeatherConditions fromCode(int code) {
        WeatherConditions[] values = values();
        if (code >= 0 && code < values.length){
            return values[code];
        }
        return NOT_AVAILABLE;
    }

    public static String getText(Context context, WeatherConditions weatherCondition){
        String[] texts = context.getResources().getStringArray(R.array.weather_condition_texts);
        int code = weatherCondition.mCode;
        if (code >= 0 && code < texts.length){
            return texts[code];
        }
        return texts[texts.length - 1];
    }
}
