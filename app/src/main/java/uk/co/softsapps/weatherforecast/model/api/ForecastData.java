package uk.co.softsapps.weatherforecast.model.api;

import java.util.ArrayList;
import java.util.List;

import uk.co.softsapps.weatherforecast.model.base.Main;
import uk.co.softsapps.weatherforecast.model.base.Weather;
import uk.co.softsapps.weatherforecast.model.base.Wind;
import uk.co.softsapps.weatherforecast.util.DateHelper;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public class ForecastData {
    public List<Weather> weather = new ArrayList<>();
    public long dt;   //date timeStamp
    public String name; //Location name
    public Main main;
    public Wind wind;

    public boolean isWithin24h() {
        return DateHelper.isWithin24h(dt);
    }

    public String getFormattedDate() {
        return DateHelper.getFormattedDate(dt);
    }

}
