package com.example.codelunch.nutrislicedata;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface NutrisliceResponse {
    void onResponse(JSONArray jsonArray);
    void onError(VolleyError error);
}
