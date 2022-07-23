package ru.vvdev.yamap.yageocode;

import com.yandex.mapkit.geometry.Point;

import ru.vvdev.yamap.utils.Callback;

public interface MapGeocodeClient {

    void geocode(final String text, final Callback<MapGeocodeItem> onSuccess, final Callback<Throwable> onError);

    void geocodePoint(final Point point, final Callback<MapGeocodeItem> onSuccess, final Callback<Throwable> onError);
}
