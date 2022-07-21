package ru.vvdev.yamap.utils;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

public class Point {
  double lat;
  double lon;

  public Point(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  public WritableMap toMap() {
    WritableMap map = Arguments.createMap();
    map.putDouble("lat", this.lat);
    map.putDouble("lon", this.lon);
    return map;
  }
}
