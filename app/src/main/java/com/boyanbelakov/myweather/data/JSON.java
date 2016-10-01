package com.boyanbelakov.myweather.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

interface JSON extends Serializable {
    JSONObject toJSON() throws JSONException;
}
