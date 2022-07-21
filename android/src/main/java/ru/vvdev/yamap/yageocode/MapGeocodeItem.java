package ru.vvdev.yamap.yageocode;

import com.yandex.mapkit.search.Address;

import java.util.List;

import ru.vvdev.yamap.utils.Point;

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
