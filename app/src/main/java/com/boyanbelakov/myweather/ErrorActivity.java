package com.boyanbelakov.myweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.boyanbelakov.myweather.dialog.ErrorDialog;
import com.boyanbelakov.myweather.service.YahooWeatherService;

public abstract class ErrorActivity extends AppCompatActivity{

    private final BroadcastReceiver mOnErrorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onErrorReceiver(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);

        IntentFilter filter = new IntentFilter(YahooWeatherService.ACTION_ERROR);
        lbm.registerReceiver(mOnErrorReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mOnErrorReceiver);
    }

    void onErrorReceiver(Intent intent){
        String userText = intent.getStringExtra(YahooWeatherService.EXTRA_ERR_USER_TEXT);
        String technicalText = intent.getStringExtra(YahooWeatherService.EXTRA_ERR_TECHNICAL_TEXT);
        showErrorDialog(userText, technicalText);
    }

    void showErrorDialog(String userText, String technicalText) {
        if (!isDestroyed()){
            ErrorDialog.newInstance(userText, technicalText)
                    .show(getSupportFragmentManager(), "ErrorDialog");
        }
    }
}
