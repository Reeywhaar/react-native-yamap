package ru.vvdev.yamap.yageocode;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.mapkit.GeoObject;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.search.Address;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.SearchType;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.search.ToponymObjectMetadata;
import com.yandex.runtime.Error;

import java.util.List;

import ru.vvdev.yamap.utils.Callback;
import ru.vvdev.yamap.utils.Point;

public class YandexMapGeocodeClient implements MapGeocodeClient {

    private final SearchManager searchManager;
    private final SearchOptions searchOptions = new SearchOptions();

  /**
     * Для Яндекса нужно указать географическую область поиска. В дефолтном варианте мы не знаем какие
     * границы для каждого конкретного города, поэтому поиск осуществляется по всему миру.
     * Для `BoundingBox` нужно указать ширину и долготу для юго-западной точки и северо-восточной
     * в градусах. Получается, что координаты самой юго-западной точки, это
     * ширина = -90, долгота = -180, а самой северо-восточной - ширина = 90, долгота = 180
     */
    private final BoundingBox defaultGeometry = new BoundingBox(
      new com.yandex.mapkit.geometry.Point(-89.999999, -179.999999),
      new com.yandex.mapkit.geometry.Point(89.999999, 179.999999)
    );

    public YandexMapGeocodeClient(Context context) {
        SearchFactory.initialize(context);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        searchOptions.setSearchTypes(SearchType.GEO.value);
        searchOptions.setGeometry(true);
        searchOptions.setDisableSpellingCorrection(true);
    }

    @Override
    public void geocode(final String text, final Callback<MapGeocodeItem> onSuccess, final Callback<Throwable> onError) {
      searchManager.submit(
        text,
        Geometry.fromBoundingBox(defaultGeometry),
        searchOptions,
        new Session.SearchListener() {
          @Override
          public void onSearchResponse(@NonNull Response response) {
            onSuccess.invoke(YandexMapGeocodeClient.extract(response));
          }

          @Override
          public void onSearchError(@NonNull Error error) {
            onError.invoke(new IllegalStateException("suggest error: " + error));
          }
        }
      );
    }

    @Nullable
    public static MapGeocodeItem extract(Response response) {
      GeoObject geoObject = null;
      com.yandex.mapkit.geometry.Point point = null;
      Address address = null;
      BoundingBox box = null;

      List<GeoObjectCollection.Item> children = response.getCollection().getChildren();
      for(GeoObjectCollection.Item item: children) {
        GeoObject _geoObject = item.getObj();
        if(_geoObject == null) continue;

        ToponymObjectMetadata _meta = _geoObject.getMetadataContainer().getItem(ToponymObjectMetadata.class);
        if(_meta == null) continue;

        com.yandex.mapkit.geometry.Point _point = null;

        List<Geometry> geometryList = _geoObject.getGeometry();

        for (Geometry geometry: geometryList) {
          com.yandex.mapkit.geometry.Point pp = geometry.getPoint();
          if(pp != null) {
            _point = pp;
            break;
          }
        }

        if(_point == null) continue;

        BoundingBox _box = _geoObject.getBoundingBox();
        if(_box == null) continue;

        geoObject = _geoObject;
        address = _meta.getAddress();
        point = _point;
        box = _box;
        break;
      }

      if(geoObject == null) return null;

      MapGeocodeItem result = new MapGeocodeItem();
      result.name = geoObject.getName();
      result.descriptionText = geoObject.getDescriptionText();
      result.formattedAddress = address.getFormattedAddress();
      result.coords = new Point(point.getLatitude(), point.getLongitude());
      result.upperCorner = new Point(box.getNorthEast().getLatitude(), box.getNorthEast().getLongitude());
      result.lowerCorner = new Point(box.getSouthWest().getLatitude(), box.getSouthWest().getLongitude());
      result.components = address.getComponents();
      return result;
    }
}
