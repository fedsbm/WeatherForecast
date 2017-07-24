package uk.co.softsapps.weatherforecast.ui.forecast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import uk.co.softsapps.weatherforecast.R;
import uk.co.softsapps.weatherforecast.model.api.ForecastData;

public class ForecastActivity extends AppCompatActivity implements ForecastActivityContract.View {

    public static final int REQUEST_CODE_RESOLUTION = 1;
    private static final String TAG = ForecastActivity.class.getSimpleName();
    public static final int LOCATION_REQUEST_CODE = 10;
    public static final String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private ForecastActivityPresenter presenter;

    private Button retryButton;
    private ProgressBar progressBar;
    private TextView messageTextView;
    private TextView conditionTextView;
    private TextView windSpeedTextView;
    private ImageView forecastImageView;
    private TextView temperatureTextView;
    private TextView windDirectionTextView;
    private LinearLayout emptyLinearLayout;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        presenter = new ForecastActivityPresenter(this);
        presenter.attachView(this);
        setupView();
    }

    private void setupView() {
        setViewReferences();
        setListeners();
    }

    private void setListeners() {
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.retryGetForecast();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.reconnectClient();
            }
        });
    }

    private void setViewReferences() {
        retryButton = (Button) findViewById(R.id.forecast_retry_button);
        progressBar = (ProgressBar) findViewById(R.id.forecast_progress_bar);
        forecastImageView = (ImageView) findViewById(R.id.forecast_image_view);
        messageTextView = (TextView) findViewById(R.id.forecast_message_text_view);
        conditionTextView = (TextView) findViewById(R.id.forecast_condition_text_view);
        windSpeedTextView = (TextView) findViewById(R.id.forecast_wind_speed_text_view);
        temperatureTextView = (TextView) findViewById(R.id.forecast_temperature_text_view);
        emptyLinearLayout = (LinearLayout) findViewById(R.id.forecast_empty_linear_layout);
        windDirectionTextView = (TextView) findViewById(R.id.forecast_wind_direction_text_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.forecast_swipe_refresh_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.buildGoogleClient(this);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void displayForecast(ForecastData forecastData) {
        conditionTextView.setText(forecastData.weather.get(0).main);
        temperatureTextView.setText(forecastData.main.getFormattedTemperature());
        windSpeedTextView.setText(forecastData.wind.getFormattedSpeed());
        windDirectionTextView.setText(forecastData.wind.getFormattedDirection());
        Glide
                .with(this)
                .load(forecastData.weather.get(0).getIconUrl(this))
                .into(forecastImageView);
    }

    @Override
    public void setEmptyView(boolean active) {
        emptyLinearLayout.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setProgressBar(boolean active) {
        progressBar.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setMainContainer(boolean active) {
        swipeRefreshLayout.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setMessageText(String message) {
        messageTextView.setText(message);
    }

    @Override
    public void dismissRefreshLayout() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermissions();
                return;
            }
            presenter.reconnectClient();
        }
    }

    @Override
    public void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_REQUEST_CODE);
    }

    @Override
    public void showLocalizedErrorDialog(ConnectionResult result) {
        // show the localized error dialog.
        GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
    }

    @Override
    public void showToast(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    presenter.reconnectClient();
                }
            } else {
                //TODO: Create a permission usage explanation
                Toast.makeText(this, R.string.location_message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
