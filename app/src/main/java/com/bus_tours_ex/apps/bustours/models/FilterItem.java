package com.bus_tours_ex.apps.bustours.models;

public class FilterItem {
    private String filterName;
    private int image;

    public FilterItem(String filterName, int image) {
        this.filterName = filterName;
        this.image = image;
    }

    public FilterItem() {
    }

    public String getFilterName() {
        return filterName;
    }

    public int getImage() {
        return image;
    }
}
