package uk.co.softsapps.weatherforecast.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import uk.co.softsapps.weatherforecast.model.api.ForecastData;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public interface OpenWeatherMapAPI {
    @GET("2.5/weather")
    Call<ForecastData> getWeather(@Query("lat") double lat,
                                  @Query("lon") double lon,
                                  @Query("units") String units,
                                  @Query("APPID") String apiKey);
}
