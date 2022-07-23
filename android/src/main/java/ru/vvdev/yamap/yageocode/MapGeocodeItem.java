package ru.vvdev.yamap.yageocode;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.search.Address;

import java.util.List;

public class MapGeocodeItem {

    public String name;
    public String descriptionText;
    public String formattedAddress;
    public Point coords;
    public Point upperCorner;
    public Point lowerCorner;
    public List<Address.Component> components;

    public MapGeocodeItem() {
    }
}
