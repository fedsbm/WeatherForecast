package uk.co.softsapps.weatherforecast;

import android.app.Application;

import uk.co.softsapps.weatherforecast.api.RestClient;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public class WeatherForecastApplication extends Application {

    private static WeatherForecastApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        RestClient.init(this);
    }

    public static WeatherForecastApplication getInstance() {
        return sInstance;
    }

}
