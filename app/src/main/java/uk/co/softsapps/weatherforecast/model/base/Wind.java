package uk.co.softsapps.weatherforecast.model.base;

import uk.co.softsapps.weatherforecast.util.Utils;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public class Wind {
    public static final String M_S = " m/s";
    public float speed;
    public float deg;

    public String getFormattedSpeed() {
        return (int) speed + M_S;
    }

    public String getFormattedDirection() {
        return Utils.getWindDirection(deg);
    }
}
