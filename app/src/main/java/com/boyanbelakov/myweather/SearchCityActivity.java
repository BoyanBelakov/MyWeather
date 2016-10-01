package com.boyanbelakov.myweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boyanbelakov.myweather.data.Place;
import com.boyanbelakov.myweather.data.Query;
import com.boyanbelakov.myweather.data.Results;
import com.boyanbelakov.myweather.data.YahooData;
import com.boyanbelakov.myweather.service.YahooWeatherService;

import java.io.Serializable;

public class SearchCityActivity extends ErrorActivity {
    private ProgressBar mProgressBar;
    private EditText mEditText;
    private ImageButton mSearchButton;
    private RecyclerView mRecyclerView;
    private PlaceAdapter mPlaceAdapter;

    private final BroadcastReceiver mOnFoundCitiesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showProgressBar(false);
            enabledUI(true);

            Serializable data = intent.getSerializableExtra(YahooWeatherService.EXTRA_YAHOO_DATA);
            YahooData yahooData = (YahooData) data;
            Query query = yahooData.getQuery();
            Results results = query.getResults();

            if (results == null){
                mPlaceAdapter.mItems = null;
                mPlaceAdapter.notifyDataSetChanged();

                String text = getString(R.string.cities_not_found);
                Snackbar.make(mRecyclerView, text, Snackbar.LENGTH_LONG).show();
                return;
            }

            mPlaceAdapter.mItems = results.getPlace();
            mPlaceAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_city);

        mPlaceAdapter = new PlaceAdapter();
        mProgressBar = (ProgressBar) findViewById(R.id.search_city_progressBar);
        mEditText = (EditText) findViewById(R.id.search_city_editText);
        mSearchButton = (ImageButton) findViewById(R.id.search_city_searchButton);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_city_recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mPlaceAdapter);

        mEditText.setText(SettingsActivity.getCityName(this));

        if (YahooWeatherService.isSearchRunning()){
            showProgressBar(true);
            enabledUI(false);
        }

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);

        IntentFilter filter = new IntentFilter(YahooWeatherService.ACTION_FOUND_CITIES);
        lbm.registerReceiver(mOnFoundCitiesReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mOnFoundCitiesReceiver);
    }

    @Override
    protected void onErrorReceiver(Intent intent) {
        super.onErrorReceiver(intent);
        showProgressBar(false);
        enabledUI(true);
    }

    private void search() {
        String text = mEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            mEditText.setError(getString(R.string.validator_err_val_empty));
            mEditText.requestFocus();
            return;
        }
        mEditText.setError(null);

        showProgressBar(true);
        enabledUI(false);

        Intent intent = YahooWeatherService.newSearchIntent(this, text);
        startService(intent);
    }

    private void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void enabledUI(boolean enabled) {
        mEditText.setEnabled(enabled);
        mSearchButton.setEnabled(enabled);
        mRecyclerView.setEnabled(enabled);
    }

    private class PlaceAdapter extends RecyclerView.Adapter<PlaceHolder>{
        private Place[] mItems;

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.length;
        }

        @Override
        public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PlaceHolder(LayoutInflater.from(SearchCityActivity.this), parent);
        }

        @Override
        public void onBindViewHolder(PlaceHolder holder, int position) {
            holder.bind(mItems[position]);
        }
    }

    private class PlaceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mNameTextView;
        private final TextView mCountryTextView;
        private Place mPlace;

        public PlaceHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(android.R.layout.simple_list_item_2, parent, false));

            mNameTextView = (TextView) itemView.findViewById(android.R.id.text1);
            mCountryTextView = (TextView) itemView.findViewById(android.R.id.text2);

            itemView.setOnClickListener(this);
        }

        public void bind(Place place) {
            mPlace = place;

            mNameTextView.setText(place.getName());
            mCountryTextView.setText(place.getCountry().getCode());
        }

        @Override
        public void onClick(View view) {
            Context context = SearchCityActivity.this;

            int id = mPlace.getID();
            SettingsActivity.setCityID(context, id);

            String code = mPlace.getCountry().getCode();
            String name = mPlace.getName();
            SettingsActivity.setCityName(context, name + ", " + code);

            Intent intent = YahooWeatherService.newFetchIntent(context);
            context.startService(intent);

            finish();
        }
    }
}
