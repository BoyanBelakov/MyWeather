package com.boyanbelakov.myweather.util;

import android.content.Context;

import com.boyanbelakov.myweather.R;
import com.boyanbelakov.myweather.SettingsActivity;
import com.boyanbelakov.myweather.data.Wind;


public class Formatter {

    public static String formatTemperature(Context context, int tempC){
        Units.Temperature u = SettingsActivity.getTemperatureUnit(context);
        switch (u){
            case c:
                return context.getString(R.string.format_temperature_c, tempC);
            case f:
                int f = Converter.convertCelsiusToFahrenheit(tempC);
                return context.getString(R.string.format_temperature_f, f);
            default:
                throw new UnsupportedOperationException("Unsupported unit: " + u.name());
        }
    }

    public static String formatWind(Context context, Wind wind) {
        float kph = wind.getSpeed();
        String direction = Converter.convertDegreeToDirection(context, wind.getDirection());

        Units.Speed u = SettingsActivity.getSpeedUnit(context);
        switch (u){
            case kph:
                return context.getString(R.string.format_wind_kph, kph, direction);
            case mph:
                float mph = Converter.convertKilometersToMiles(kph);
                return context.getString(R.string.format_wind_mph, mph, direction);
            default:
                throw new UnsupportedOperationException("Unsupported unit: " + u.name());
        }
    }

    public static String formatPressure(Context context, float pressure) {
        Units.Pressure u = SettingsActivity.getPressureUnit(context);
        switch (u){
            case mb:
                return context.getString(R.string.format_pressure_mb, pressure);
            case in_:
                float inches = Converter.convertMillibarsToInches(pressure);
                return context.getString(R.string.format_pressure_in, inches);
            default:
                throw new UnsupportedOperationException("Unsupported unit: " + u.name());
        }
    }

    public static String formatVisibility(Context context, float visibility) {
        Units.Distance u = SettingsActivity.getDistanceUnit(context);
        switch (u){
            case km:
                return context.getString(R.string.format_distance_km, visibility);
            case mi:
                float mi = Converter.convertKilometersToMiles(visibility);
                return context.getString(R.string.format_distance_mi, mi);
            default:
                throw new UnsupportedOperationException("Unsupported unit: " + u.name());
        }
    }
}
