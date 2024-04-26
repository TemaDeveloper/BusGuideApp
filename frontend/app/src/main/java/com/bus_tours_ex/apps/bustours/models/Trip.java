package com.bus_tours_ex.apps.bustours.models;

public class Trip {

    private String title;
    private String image;
    private int price;
    private String plan;
    private String pickUp;
    private String category;

    public Trip(String title, int price, String plan, String pickUp, String category) {
        this.title = title;
        this.price = price;
        this.plan = plan;
        this.pickUp = pickUp;
        this.category = category;
    }

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
