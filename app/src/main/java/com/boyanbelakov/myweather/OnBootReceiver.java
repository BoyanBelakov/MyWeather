package com.boyanbelakov.myweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.boyanbelakov.myweather.service.YahooWeatherService;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
            YahooWeatherService.setServiceAlarm(context, true);
        }
    }
}
