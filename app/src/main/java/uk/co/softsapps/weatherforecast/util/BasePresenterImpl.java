package uk.co.softsapps.weatherforecast.util;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;

public class BasePresenterImpl<V> implements BasePresenter<V> {

    private WeakReference<V> mView;

    @UiThread
    @Override
    public void attachView(V view) {
        mView = new WeakReference<V>(view);
    }

    @UiThread
    public boolean isViewAttached() {
        return (mView != null) && (mView.get() != null);
    }

    @UiThread
    @Nullable
    public V getView() {
        return (mView != null) ? mView.get() : null;
    }

    @UiThread
    @Override
    public void detachView() {
        if (mView != null) {
            mView.clear();
            mView = null;
        }
    }
}
