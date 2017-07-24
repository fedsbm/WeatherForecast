package uk.co.softsapps.weatherforecast.api;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.softsapps.weatherforecast.BuildConfig;
import uk.co.softsapps.weatherforecast.R;
import uk.co.softsapps.weatherforecast.util.Utils;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public class RestClient {

    private static OpenWeatherMapAPI weatherAPI;

    public static void init(Context context) {
        weatherAPI = getRetrofit(context).create(OpenWeatherMapAPI.class);
    }

    private static Retrofit getRetrofit(Context context) {
        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient(context))
                .build();
    }

    private static OkHttpClient getHttpClient(Context context) {
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
                .addInterceptor(getHttpDebugInterceptor())
                .addInterceptor(getCacheInterceptor(context))
                .cache(getCache(context));

        return httpBuilder.build();
    }

    private static Interceptor getCacheInterceptor(final Context context) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (!Utils.isNetworkAvailable(context)) {
                    Request request = chain.request();

                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(24, TimeUnit.HOURS)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();

                    return chain.proceed(request);
                } else {
                    return chain.proceed(chain.request());
                }
            }
        };
    }

    private static Interceptor getHttpDebugInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        HttpLoggingInterceptor.Level body =
                BuildConfig.DEBUG ?
                        HttpLoggingInterceptor.Level.BODY :
                        HttpLoggingInterceptor.Level.NONE;
        loggingInterceptor.setLevel(body);
        return loggingInterceptor;
    }

    private static Cache getCache(Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(context.getCacheDir(), cacheSize);
    }

    public static OpenWeatherMapAPI getApiService() {
        return weatherAPI;
    }
}