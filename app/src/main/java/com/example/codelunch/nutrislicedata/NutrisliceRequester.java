package com.example.codelunch.nutrislicedata;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class NutrisliceRequester {
    private final String NUTRISLICE_URL = "https://sla-xvn.nutrislice.com";

    private String school;

    private RequestQueue queue;

    // CONSTRUCTORS
    public NutrisliceRequester(Context context, String school) {
        this.school = school;
        queue = Volley.newRequestQueue(context);
    }

    // Setters
    public void setSchool(String school) {
        this.school = school;
    }


    // Methods
    public void getFoods(Calendar calendar, NutrisliceResponse nutrisliceResponse) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String url = NUTRISLICE_URL + "/menu/api/weeks/school/" + school + "/menu-type/lunch/" + year + "/" + month + "/" + day;
        Log.d("TAG", "URL: " + url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    String dayCheckFormat = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
                    try {
                        JSONArray days = response.getJSONArray("days");
                        for (int i = 0; i < days.length(); i++) {
                            JSONObject currentDay = days.getJSONObject(i);
                            if (currentDay.getString("date").equals(dayCheckFormat))
                                convertData(nutrisliceResponse, currentDay.getJSONArray("menu_items"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        new JSONObject();
                        nutrisliceResponse.onResponse(new JSONArray());
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    nutrisliceResponse.onError(error);
                }
            }
        );
        queue.add(request);
    }

    public void convertData(NutrisliceResponse nutrisliceResponse, JSONArray jsonArray) {
        JSONArray out = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject menuItem = jsonArray.getJSONObject(i);
                ///Log.d("TAG", "convertData: " + menuItem);
                // Section title
                if (menuItem.getBoolean("is_section_title")) {
                    JSONObject sectionTitle = new JSONObject();
                    sectionTitle.put("text", menuItem.getString("text"));
                    sectionTitle.put("category-items", new JSONArray());
                    out.put(out.length(), sectionTitle);
                }else  if (!menuItem.getBoolean("is_holiday") && !menuItem.getBoolean("is_station_header") && !menuItem.getBoolean("blank_line")) {
                    //Log.d("TAG", "convertData: " + menuItem);
                    String foodName = "";
                    if (menuItem.isNull("food"))
                        foodName = menuItem.getString("text");
                    else
                        foodName = menuItem.getJSONObject("food").getString("name");

                    JSONObject sectionTitle = out.getJSONObject(out.length()-1);
                    JSONArray categoryItems = sectionTitle.getJSONArray("category-items");
                    JSONObject foodObject = new JSONObject();
                    foodObject.put("name",foodName);
                    categoryItems.put(categoryItems.length(), foodObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("TAG", "exceptioned: ");
            }
        }

        //Log.d("TAG", "convertData: " + out);

        nutrisliceResponse.onResponse(out);
    }
}
