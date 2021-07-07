package com.example.codelunch.preference;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TimePicker;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.example.codelunch.activities.MainActivity;

public class TimePreference extends Preference {
    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String timeString = sharedPreferences.getString(getKey(),"00:00");
        setSummary(timeString);
    }

    public TimePreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.d("TAG", "TimePreference: clicked");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String timeString = sharedPreferences.getString(getKey(),"00:00");
        String[] timeArray = timeString.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int min = Integer.parseInt(timeArray[1]);

        //TODO Set and save time
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String out = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                        editor.putString(getKey(), hourOfDay + ":" + minute);
                        editor.commit();
                        setSummary(out);

                        //MainActivity.changeRepeatingTime(PreferenceManager.getDefaultSharedPreferences(getContext()));
                    }
                }, hour, min,true);
        timePickerDialog.show();
    }
}
