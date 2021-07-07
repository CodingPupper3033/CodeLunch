package com.example.codelunch.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.android.volley.VolleyError;
import com.example.codelunch.R;
import com.example.codelunch.activities.MainActivity;
import com.example.codelunch.nutrislicedata.NutrisliceRequester;
import com.example.codelunch.nutrislicedata.NutrisliceResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class NotificationHelper {

    private Context mContext;
    private static final String NOTIFICATION_CHANNEL_ID = MainActivity.CHANNEL_ID;
    NotificationCompat.Builder builder;

    NotificationHelper(Context context) {
        mContext = context;
        builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notification_icon);
    }

    public void createNotification()
    {

        Intent intent = new Intent(mContext , MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        firstNotification();
        requestFood();
    }

    public void showNotification() {
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0 /* Request Code */, builder.build());
    }

    public void firstNotification() {
        builder
                .setContentTitle("Asking the Chef!")
                .setColor(16737792); // Orange
        showNotification();
    }

    public void errorNotification(VolleyError error) {
        builder
                .setContentTitle("The Chef was not there; We'll try again later")
                .setColor(16737792) // Orange
                .setStyle(new NotificationCompat.BigTextStyle()
                        //.bigText(error.getClass().getName()));
                        .bigText(error.getMessage()));
        showNotification();
    }

    public void noFoodNotification(String reason) {
        builder
                .setContentTitle("No Food Today!")
                .setColor(16737792) // Orange
                .setStyle(new NotificationCompat.BigTextStyle()
                        //.bigText(error.getClass().getName()));
                        .bigText(reason));
        showNotification();
    }

    public void foodNotification(String bigText) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        builder
                .setContentTitle("Lunch Menu")
                .setColor(16737792) // Orange
                .setStyle(new NotificationCompat.BigTextStyle()
                        //.bigText(error.getClass().getName()));
                        .bigText(bigText))
                .setContentIntent(pendingIntent);
        showNotification();
    }

    private void errorInReceivingFood(VolleyError error) {
        errorNotification(error);
        //TODO Re-ask
    }

    public void requestFood() {
        //TODO editable school
        NutrisliceRequester requester = new NutrisliceRequester(mContext,"xaverian-brothers-high-school");
        Calendar calendar2 = Calendar.getInstance();
        requester.getFoods(calendar2, new NutrisliceResponse() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    receivedFoodList(jsonArray);
                } catch (JSONException e) {
                    errorInReceivingFood(new VolleyError("Parsing Error"));
                }
            }

            @Override
            public void onError(VolleyError error) {
                errorInReceivingFood(error);
            }
        });
    }

    private void receivedFoodList(JSONArray categories) throws JSONException {
        JSONArray convertedCategories = convertUsingList(categories);
        Log.d("TAG", "receivedFoodList: ");
        if (convertedCategories.length() == 0) {
            Log.d("TAG", "receivedFoodList: no length");
            // TODO get reason
            noFoodNotification("");
        } else {
            String bigText = "";
            for (int i = 0; i < convertedCategories.length(); i++) {
                JSONObject currentCategory = convertedCategories.getJSONObject(i);
                bigText += currentCategory.getString("text") + "\n";
                JSONArray currentCategoryItems = currentCategory.getJSONArray("category-items");
                for (int j = 0; j < currentCategoryItems.length(); j++) {
                    JSONObject currentItem = currentCategoryItems.getJSONObject(j);
                    bigText += " ∟ " + currentItem.getString("name") + "\n";
                }
            }
            Log.d("TAG", "receivedFoodList: " + convertedCategories);
            Log.d("TAG", "receivedFoodList: " + bigText);
            foodNotification(bigText);
        }
    }

    private JSONArray convertUsingList(JSONArray categories) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean blacklist = preferences.getBoolean("blacklist",false);
        if (blacklist)
            return convertUsingBlacklist(preferences, categories);
        else
            return convertUsingWhitelist(preferences, categories);
    }

    private JSONArray convertUsingBlacklist(SharedPreferences preferences, JSONArray categories) {
        try {
            String listString = "[\"" + preferences.getString("listBlacklist","") + "\"]";
            listString = listString
                    .replaceAll(", ", ",")
                    .replaceAll(",", "\",\"");
            JSONArray listArray = new JSONArray(listString);
            //Log.d("TAG", "convertUsingBlacklist: " + categories.length());
            JSONArray out = new JSONArray();
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                String categoryName = category.getString("text");

                boolean found = false;
                for (int j = 0; j < listArray.length() && !found; j++) {
                    String categoryListName = listArray.getString(j);
                    if (categoryName.equals(categoryListName)) {
                        found = true;
                    }
                }
                if (!found) {
                    out.put(category);
                }
            }
            return out;
        } catch (JSONException e) {
        }
        return categories;
    }

    private JSONArray convertUsingWhitelist(SharedPreferences preferences, JSONArray categories) {
        try {
            String listString = "[\"" + preferences.getString("listBlacklist","") + "\"]";
            listString = listString
                    .replaceAll(", ", ",")
                    .replaceAll(",", "\",\"");
            JSONArray listArray = new JSONArray(listString);
            //Log.d("TAG", "convertUsingBlacklist: " + categories.length());
            JSONArray out = new JSONArray();
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                String categoryName = category.getString("text");

                boolean found = false;
                for (int j = 0; j < listArray.length() && !found; j++) {
                    String categoryListName = listArray.getString(j);
                    if (categoryName.equals(categoryListName)) {
                        found = true;
                    }
                }
                if (found) {
                    out.put(category);
                }
            }
            return out;
        } catch (JSONException e) {
        }
        return new JSONArray();
    }
}