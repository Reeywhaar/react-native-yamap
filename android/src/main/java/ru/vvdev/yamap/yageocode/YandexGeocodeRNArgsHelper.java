package ru.vvdev.yamap.yageocode;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.yandex.mapkit.search.Address;

public final class YandexGeocodeRNArgsHelper {
    WritableMap createResultItemFrom(MapGeocodeItem data) {
        final WritableMap result = Arguments.createMap();
        final WritableArray components = Arguments.createArray();
        result.putString("name", data.name);
        result.putString("descriptionText", data.descriptionText);
        result.putString("formattedAddress", data.formattedAddress);
        result.putMap("coords", data.coords.toMap());
        result.putMap("upperCorner", data.upperCorner.toMap());
        result.putMap("lowerCorner", data.lowerCorner.toMap());

        for(Address.Component component: data.components) {
          WritableMap item = Arguments.createMap();
          WritableArray kinds = Arguments.createArray();
          item.putString("name", component.getName());
          for(Address.Component.Kind kind: component.getKinds()) {
            kinds.pushString(this.getKindString(kind));
          }
          item.putArray("kinds", kinds);
          components.pushMap(item);
        }

        result.putArray("components", components);
        return result;
    }

    private String getKindString(Address.Component.Kind kind) {
      switch (kind) {
        case COUNTRY:
          return "country";
        case REGION:
          return "region";
        case PROVINCE:
          return "area";
        case AREA:
          return "province";
        case LOCALITY:
          return "locality";
        case DISTRICT:
          return "district";
        case STREET:
          return "street";
        case HOUSE:
          return "house";
        case ENTRANCE:
          return "entrance";
        case ROUTE:
          return "route";
        case STATION:
          return "station";
        case METRO_STATION:
          return "metro";
        case RAILWAY_STATION:
          return "railway";
        case VEGETATION:
          return "vegetation";
        case HYDRO:
          return "hydro";
        case AIRPORT:
          return "airport";
        case OTHER:
          return "other";
        default:
          return "unknown";
      }
    }
}
