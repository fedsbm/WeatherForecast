package uk.co.softsapps.weatherforecast.ui.forecast;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.softsapps.weatherforecast.R;
import uk.co.softsapps.weatherforecast.api.RestClient;
import uk.co.softsapps.weatherforecast.model.api.ForecastData;
import uk.co.softsapps.weatherforecast.util.BasePresenterImpl;
import uk.co.softsapps.weatherforecast.util.Utils;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public class ForecastActivityPresenter extends BasePresenterImpl<ForecastActivityContract.View>
        implements ForecastActivityContract.Presenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String METRIC = "metric";
    private static final String TAG = ForecastActivityPresenter.class.getSimpleName();

    private ForecastActivityModel model;
    private GoogleApiClient client;
    private Location location;
    private Context context;
    private boolean isDisplayingData;

    public ForecastActivityPresenter(Context context) {
        model = new ForecastActivityModel();
        this.context = context;
    }

    @Override
    public void getForecast() {
        showLoading();
        retrieveForecastData(new Callback<ForecastData>() {
            @Override
            public void onResponse(Call<ForecastData> call, Response<ForecastData> response) {
                if (isViewAttached()) {
                    if (response.body() == null) {
                        checkStoredData();
                    } else {
                        updateForecastData(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<ForecastData> call, Throwable t) {
                if (isViewAttached()) {
                    checkStoredData();
                }
            }
        });
    }

    private void checkStoredData() {
        ForecastData forecastData = model.getStoredForecast();
        if (forecastData == null || !forecastData.isWithin24h()) {
            showEmptyResult();
        } else {
            showRequestResult();
            getView().displayForecast(forecastData);
            getView().setMessageText(context.getString(R.string.forecast_out_of_date, forecastData.name, forecastData.getFormattedDate()));
        }
    }

    @Override
    public void retryGetForecast() {
        if (Utils.isNetworkAvailable(context)) {
            getForecast();
        } else {
            getView().showToast( R.string.internet_unavailable);
        }
    }

    public void refreshForecast() {
        if (Utils.isNetworkAvailable(context)) {
            retrieveForecastData(new Callback<ForecastData>() {
                @Override
                public void onResponse(Call<ForecastData> call, Response<ForecastData> response) {
                    if (isViewAttached()) {
                        getView().dismissRefreshLayout();
                        if (response.body() == null) {
                            getView().showToast(R.string.udapte_failed);
                        } else {
                            updateForecastData(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ForecastData> call, Throwable t) {
                    if (isViewAttached()) {
                        getView().dismissRefreshLayout();
                        getView().showToast(R.string.udapte_failed);
                    }
                }
            });
        } else {
            getView().dismissRefreshLayout();
            getView().showToast(R.string.internet_unavailable);
        }
    }

    private void updateForecastData(ForecastData forecastData) {
        showRequestResult();
        model.setForecast(forecastData);
        getView().displayForecast(model.getStoredForecast());
        if (Utils.isNetworkAvailable(context)) {
            getView().setMessageText(null);
        } else {
            getView().setMessageText(context.getString(R.string.forecast_out_of_date, forecastData.name, forecastData.getFormattedDate()));
        }
    }

    private void retrieveForecastData(Callback<ForecastData> callback) {
        final String apiKey = context.getString(R.string.api_key);
        Call<ForecastData> call =
                RestClient
                        .getApiService()
                        .getWeather(location == null ? 0 : location.getLatitude(),
                                location == null ? 0 : location.getLongitude(),
                                METRIC,
                                apiKey);
        call.enqueue(callback);
    }

    @Override
    public void buildGoogleClient(Context context) {
        if (client == null) {
            client = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }
        client.connect();
    }

    @Override
    public void reconnectClient() {
        client.reconnect();
    }

    private void showLoading() {
        getView().setEmptyView(false);
        getView().setProgressBar(true);
        getView().setMainContainer(false);
    }

    private void showEmptyResult() {
        getView().setEmptyView(true);
        getView().setProgressBar(false);
        getView().setMainContainer(false);
    }

    private void showRequestResult() {
        isDisplayingData = true;
        getView().setEmptyView(false);
        getView().setProgressBar(false);
        getView().setMainContainer(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (isViewAttached()) {
            Log.d(TAG, "GoogleApiClient Connected");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getView().requestLocationPermissions();
                return;
            }
            location = LocationServices.FusedLocationApi.getLastLocation(client);
            if (isDisplayingData) {
                refreshForecast();
            } else {
                getForecast();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            getView().showLocalizedErrorDialog(result);
            return;
        }
        try {
            result.startResolutionForResult((Activity) context, ForecastActivity.REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }
}
