package uk.co.softsapps.weatherforecast.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return (activeNetwork != null) && activeNetwork.isConnected();
    }

    public static String getWindDirection(float deg) {
        String directions[] = {"North", "North East", "East", "South East", "South", "South West", "West", "North West", "North"};
        return directions[Math.round(((deg % 360) / 45))];
    }
}
