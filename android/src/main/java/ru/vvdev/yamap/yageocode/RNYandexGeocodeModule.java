package ru.vvdev.yamap.yageocode;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import android.content.Context;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.yandex.mapkit.geometry.Point;

import ru.vvdev.yamap.utils.Callback;

public class RNYandexGeocodeModule extends ReactContextBaseJavaModule {

    private static final String ERR_NO_REQUEST_ARG = "YANDEX_SUGGEST_ERR_NO_REQUEST_ARG";
    private static final String ERR_GEOCODE_FAILED = "YANDEX_GEOCODE_ERR_GEOCODE_FAILED";

    @Nullable
    private MapGeocodeClient geocodeClient;
    private final YandexGeocodeRNArgsHelper argsHelper = new YandexGeocodeRNArgsHelper();

    public RNYandexGeocodeModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "YamapGeocode";
    }

    @ReactMethod
    public void geocode(final String text, final Promise promise) {
        if (text == null) {
            promise.reject(ERR_NO_REQUEST_ARG, "suggest request: text arg is not provided");
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getGeocodeClient(getReactApplicationContext()).geocode(text,
                        new Callback<MapGeocodeItem>() {
                            @Override
                            public void invoke(MapGeocodeItem result) {
                                promise.resolve(argsHelper.createResultItemFrom(result));
                            }
                        },
                        new Callback<Throwable>() {
                            @Override
                            public void invoke(Throwable e) {
                                promise.reject(ERR_GEOCODE_FAILED, "suggest request: " + e.getMessage());
                            }
                        });
            }
        });
    }

    @ReactMethod
    public void geocodePoint(ReadableArray point, final Promise promise) {
        if (point == null) {
            promise.reject(ERR_NO_REQUEST_ARG, "suggest request: text arg is not provided");
            return;
        }
        Point ypoint = new Point(point.getDouble(0), point.getDouble(1));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getGeocodeClient(getReactApplicationContext()).geocodePoint(ypoint,
                        new Callback<MapGeocodeItem>() {
                            @Override
                            public void invoke(MapGeocodeItem result) {
                                promise.resolve(argsHelper.createResultItemFrom(result));
                            }
                        },
                        new Callback<Throwable>() {
                            @Override
                            public void invoke(Throwable e) {
                                promise.reject(ERR_GEOCODE_FAILED, "suggest request: " + e.getMessage());
                            }
                        });
            }
        });
    }

    private MapGeocodeClient getGeocodeClient(Context context) {
        if (geocodeClient == null) {
            geocodeClient = new YandexMapGeocodeClient(context);
        }
        return geocodeClient;
    }
}
