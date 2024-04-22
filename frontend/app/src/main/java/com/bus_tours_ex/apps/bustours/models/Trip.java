package com.bus_tours_ex.apps.bustours.models;

public class Trip {

    private String title;
    private String image;
    private int price;

    public Trip(String title, String image, int price) {
        this.title = title;
        this.image = image;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public int getPrice() {
        return price;
    }
}
