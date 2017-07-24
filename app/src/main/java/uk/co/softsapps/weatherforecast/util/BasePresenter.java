package uk.co.softsapps.weatherforecast.util;

import android.support.annotation.UiThread;

public interface BasePresenter<V> {

    @UiThread
    void attachView(V view);

    @UiThread
    void detachView();
}
