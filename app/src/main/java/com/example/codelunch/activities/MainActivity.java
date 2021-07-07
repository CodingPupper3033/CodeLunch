package com.example.codelunch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.codelunch.NutrisliceWebViewMaker;
import com.example.codelunch.R;
import com.example.codelunch.notification.NotifyReceiver;
import com.example.codelunch.nutrislicedata.NutrisliceResponse;

import java.util.Calendar;

// TODO Show lunch activity | School Picker | About menu

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "CodeLunch";
    public static PendingIntent pendingIntent;
    public static AlarmManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the title bar
        try{ this.getSupportActionBar().hide(); } catch (NullPointerException e){}
        setContentView(R.layout.activity_main);
        buildNotificationChannels();

        // Action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_notification_icon);
        setSupportActionBar(toolbar);

        // Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Set up Alarm
        alarmMaker(sharedPreferences);

        // Website
        setupWebView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeRepeatingTime(SharedPreferences sharedPreferences) {
        alarmMaker(sharedPreferences);
    }

    private void buildNotificationChannels() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Lunch Updates", importance);
        channel.setShowBadge(false);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

    }

    private void setupWebView() {
        // School
        String school = "xaverian-brothers-high-school";

        // Calendar
        Calendar calendar = Calendar.getInstance();

        // URL
        String url = NutrisliceWebViewMaker.getURL(school,calendar);

        WebView myWebView = findViewById(R.id.webView);
        System.out.println(url);

        myWebView.getSettings().setJavaScriptEnabled(true);

        myWebView.loadUrl("https://www.google.com");
        myWebView.loadUrl("https://sla-xvn.nutrislice.com/");

        // TODO NOTHING WORKS HECKING WEBVIEWS
    }

    public void alarmMaker(SharedPreferences sharedPreferences) {
        String timeString = sharedPreferences.getString("time","00:00");
        String[] timeArray = timeString.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int min = Integer.parseInt(timeArray[1]);

        // Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        Log.d("TAG", "Time set: " + calendar.getTime());

        Intent intent = new Intent(getApplicationContext(), NotifyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }


    }
}