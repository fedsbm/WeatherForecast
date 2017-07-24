package uk.co.softsapps.weatherforecast.model.base;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import uk.co.softsapps.weatherforecast.R;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public class Weather {
    public static final String PNG = ".png";
    public String main;
    public String icon;

    public Weather(){}

    public String getIconUrl(Context context) {
        Log.d("Fernando", context.getString(R.string.image_url) + icon + PNG);
        if (TextUtils.isEmpty(icon)) {
            return null;
        } else {
            return context.getString(R.string.image_url) + icon + PNG;
        }
    }
}
