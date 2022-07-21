package ru.vvdev.yamap.yageocode;

import ru.vvdev.yamap.utils.Callback;

public interface MapGeocodeClient {

    /**
     * Получить саджесты по тексту {@code text}.
     * Вернуть результат в метод {@code onSuccess} в случае успеха, в случае неудачи в {@code onError}
     */
    void geocode(final String text, final Callback<MapGeocodeItem> onSuccess, final Callback<Throwable> onError);
}
