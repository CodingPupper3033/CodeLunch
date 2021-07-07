package com.example.codelunch;

import java.util.Calendar;

public class NutrisliceWebViewMaker {
    public static String getURL(String school, Calendar calendar) {
        String url = "https://sla-xvn.nutrislice.com/menu/" + school + "/lunch/" + calendar.get(Calendar.YEAR) + "-" + String.format("%02d", calendar.get(Calendar.MONTH)+1) + "-" + String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH));
        return url;
    }
}
