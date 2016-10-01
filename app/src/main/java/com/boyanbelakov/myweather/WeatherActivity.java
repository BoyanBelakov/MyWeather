package com.boyanbelakov.myweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boyanbelakov.myweather.customview.ForecastChart;
import com.boyanbelakov.myweather.data.Astronomy;
import com.boyanbelakov.myweather.data.Atmosphere;
import com.boyanbelakov.myweather.data.Channel;
import com.boyanbelakov.myweather.data.Condition;
import com.boyanbelakov.myweather.data.Forecast;
import com.boyanbelakov.myweather.data.InternalStorage;
import com.boyanbelakov.myweather.data.Item;
import com.boyanbelakov.myweather.data.Location;
import com.boyanbelakov.myweather.data.Results;
import com.boyanbelakov.myweather.data.Wind;
import com.boyanbelakov.myweather.data.YahooData;
import com.boyanbelakov.myweather.dialog.AboutDialog;
import com.boyanbelakov.myweather.service.YahooWeatherService;
import com.boyanbelakov.myweather.util.Formatter;
import com.boyanbelakov.myweather.util.WeatherConditions;

import java.io.Serializable;
import java.util.Date;

public class WeatherActivity extends ErrorActivity {
    private TextView mFooterTextView;
    private TextView mIconTextView;
    private TextView mCityNameTextView;
    private TextView mDescriptionTextView;
    private TextView mTemperatureTextView;
    private TextView mWindTextView;
    private TextView mHumidityTextView;
    private TextView mPressureTextView;
    private TextView mVisibilityTextView;
    private TextView mSunriseTextView;
    private TextView mSunsetTextView;

    private ForecastChart mForecastChart;
    private View mEmptyView;
    private View mWeatherContent;
    private ProgressBar mProgressBar;

    private YahooData mWeatherData;

    private final BroadcastReceiver mOnUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showProgressBar(false);

            Serializable data = intent.getSerializableExtra(YahooWeatherService.EXTRA_YAHOO_DATA);
            mWeatherData = (YahooData) data;
            updateUI();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!YahooWeatherService.isServiceAlarmOn(this)){
            startFetchService();
            YahooWeatherService.setServiceAlarm(this, true);
        }

        setContentView(R.layout.activity_weather);

        mFooterTextView = (TextView) findViewById(R.id.weather_footer);
        mIconTextView = (TextView) findViewById(R.id.weather_icon);
        mCityNameTextView = (TextView) findViewById(R.id.weather_cityName);
        mDescriptionTextView = (TextView) findViewById(R.id.weather_description);
        mTemperatureTextView = (TextView) findViewById(R.id.weather_temperature);
        mWindTextView = (TextView) findViewById(R.id.weather_wind);
        mHumidityTextView = (TextView) findViewById(R.id.weather_humidity);
        mPressureTextView = (TextView) findViewById(R.id.weather_pressure);
        mVisibilityTextView = (TextView) findViewById(R.id.weather_visibility);
        mSunriseTextView = (TextView) findViewById(R.id.weather_sunrise);
        mSunsetTextView = (TextView) findViewById(R.id.weather_sunset);
        mForecastChart = (ForecastChart) findViewById(R.id.weather_forecastChart);
        mEmptyView = findViewById(R.id.weather_emptyView);
        mWeatherContent = findViewById(R.id.weather_content);
        mProgressBar = (ProgressBar) findViewById(R.id.weather_progressBar);

        if (mIconTextView != null){// in landscape mode is null!
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/weather.ttf");
            mIconTextView.setTypeface(font);
        }

        showEmptyView(true);
        showProgressBar(true);

        new WeatherTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);

        IntentFilter filter = new IntentFilter(YahooWeatherService.ACTION_UPDATE_WEATHER);
        lbm.registerReceiver(mOnUpdateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mOnUpdateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                showProgressBar(true);
                startFetchService();
                return true;
            case R.id.action_about:
                AboutDialog.newInstance().show(getSupportFragmentManager(), "AboutDialog");
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onErrorReceiver(Intent intent) {
        super.onErrorReceiver(intent);
        showProgressBar(false);
    }

    private void startFetchService(){
        Intent intent = YahooWeatherService.newFetchIntent(this);
        startService(intent);
    }

    private void updateUI() {
        Results results = mWeatherData.getQuery().getResults();
        if (results == null){
            showEmptyView(true);
            return;
        }

        showEmptyView(false);

        Channel channel = results.getChannel();
        Date lastBuildDate = channel.getLastBuildDate();
        Location location = channel.getLocation();
        Wind wind = channel.getWind();
        Atmosphere atmosphere = channel.getAtmosphere();
        Astronomy astronomy = channel.getAstronomy();
        Item item = channel.getItem();
        Condition condition = item.getCondition();
        Forecast[] forecast = item.getForecast();

        if (mIconTextView != null){
            int code = condition.getCode();
            String icon = WeatherConditions.fromCode(code).getIcon();
            mIconTextView.setText(icon);
        }

        if (mCityNameTextView != null){
            String cityCountry = location.getCity() + ", " + location.getCountry();
            mCityNameTextView.setText(cityCountry);
        }

        if (mDescriptionTextView != null){
            int code = condition.getCode();
            String text = WeatherConditions.getText(this, WeatherConditions.fromCode(code));
            mDescriptionTextView.setText(text);
        }

        if (mTemperatureTextView != null){
            String temp = Formatter.formatTemperature(this, condition.getTemp());
            mTemperatureTextView.setText(temp);
        }

        if (mWindTextView != null){
            String windText = Formatter.formatWind(this, wind);
            mWindTextView.setText(windText);
        }

        if (mPressureTextView != null){
            String pressure = Formatter.formatPressure(this, atmosphere.getPressure());
            mPressureTextView.setText(pressure);
        }

        if (mHumidityTextView != null){
            String humidity = getString(R.string.format_humidity, atmosphere.getHumidity());
            mHumidityTextView.setText(humidity);
        }

        if (mVisibilityTextView != null){
            String visibility = Formatter.formatVisibility(this, atmosphere.getVisibility());
            mVisibilityTextView.setText(visibility);
        }

        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);

        if (mSunriseTextView != null){
            String sunrise = timeFormat.format(astronomy.getSunrise());
            mSunriseTextView.setText(sunrise);
        }

        if (mSunsetTextView != null){
            String sunset = timeFormat.format(astronomy.getSunset());
            mSunsetTextView.setText(sunset);
        }

        mForecastChart.setData(forecast);

        String date = DateFormat.getMediumDateFormat(this).format(lastBuildDate);
        String time = timeFormat.format(lastBuildDate);
        String dateTime = date + " " + time;
        mFooterTextView.setText(dateTime);
    }

    private void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyView(boolean show) {
        mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        mWeatherContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private class WeatherTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... voids) {
            try {
                return InternalStorage.getInstance(WeatherActivity.this).load();
            } catch (Exception e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if (!YahooWeatherService.isFetchRunning()){
                showProgressBar(false);
            }

            if (mWeatherData != null){// mWeatherData is init in mOnUpdateReceiver.onReceive
                return;
            }

            if (o == null) {
                return;
            }

            if (o instanceof Exception){
                String userText = getString(R.string.err_user_text);
                String technicalText = ((Exception) o).getMessage();
                showErrorDialog(userText, technicalText);
            }else {
                mWeatherData = (YahooData) o;
                updateUI();
            }
        }
    }
}
