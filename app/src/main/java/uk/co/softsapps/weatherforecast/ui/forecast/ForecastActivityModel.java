package uk.co.softsapps.weatherforecast.ui.forecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import uk.co.softsapps.weatherforecast.WeatherForecastApplication;
import uk.co.softsapps.weatherforecast.model.api.ForecastData;
import uk.co.softsapps.weatherforecast.model.base.Weather;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public class ForecastActivityModel implements ForecastActivityContract.Model {

    public static final String LAST_FORECAST = "last_forecast";

    public ForecastActivityModel() {
    }

    @Override
    public void setForecast(ForecastData forecastData) {
        if (forecastData == null) {
            return;
        }
        if (forecastData.weather.isEmpty()) {
            forecastData.weather.add(new Weather());
        }
        String json = new Gson().toJson(forecastData);
        getSharedPreferences().edit().putString(LAST_FORECAST, json).apply();
    }

    @Override
    public ForecastData getStoredForecast() {
        String json = getSharedPreferences().getString(LAST_FORECAST, null);
        return TextUtils.isEmpty(json) ? null : new Gson().fromJson(json, ForecastData.class);
    }

    private SharedPreferences getSharedPreferences() {
        return WeatherForecastApplication.getInstance().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE);
    }
}
