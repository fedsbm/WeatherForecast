package uk.co.softsapps.weatherforecast.model.base;


/**
 * Created by Fernando Bonet on 23/07/2017.
 */


public class Main {
    public static final String CELSIUS = "°C";
    public String temp;

    public String getFormattedTemperature(){
        return temp + CELSIUS;
    }
}
