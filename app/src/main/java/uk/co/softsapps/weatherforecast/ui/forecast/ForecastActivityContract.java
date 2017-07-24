package uk.co.softsapps.weatherforecast.ui.forecast;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;

import uk.co.softsapps.weatherforecast.model.api.ForecastData;
import uk.co.softsapps.weatherforecast.util.BasePresenter;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public interface ForecastActivityContract {

    interface View {

        void displayForecast(ForecastData forecastData);

        void setEmptyView(boolean active);

        void setProgressBar(boolean active);

        void setMainContainer(boolean active);

        void setMessageText(String message);

        void dismissRefreshLayout();

        void requestLocationPermissions();

        void showLocalizedErrorDialog(ConnectionResult result);

        void showToast(int message);
    }

    interface Model {
        void setForecast(ForecastData forecastData);

        ForecastData getStoredForecast();
    }

    interface Presenter extends BasePresenter<View> {
        void getForecast();

        void retryGetForecast();

        void buildGoogleClient(Context context);

        void reconnectClient();
    }
}
