package com.example.codelunch.preference;

import android.app.PendingIntent;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.example.codelunch.activities.MainActivity;

public class ButtonPreference extends Preference {

    public ButtonPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ButtonPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ButtonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonPreference(Context context) {
        super(context);
    }


    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
    }

    public void onClick() {
        try {
            MainActivity.pendingIntent.send();
            Log.d("TAG", "onClick: Sent!");
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
