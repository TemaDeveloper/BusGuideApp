package com.bus_tours_ex.apps.bustours.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trip {

    //image, title, price, category, pick_up_points
    //avatar_img, name, last_name, regular_number, email, whatsapp_number, tg_tag, viber_number, trip_id
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("plan")
    @Expose
    private String plan;
    @SerializedName("pick_up_points")
    @Expose
    private String[] pickUp;
    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("organizator")
    @Expose
    private Organizator organizator;
    @SerializedName("reviews")
    @Expose
    private Reviews[] reviews;

    public Trip(String title, int price, String plan, String[] pickUp, String category, Organizator organizator, Reviews[] reviews) {
        this.title = title;
        this.price = price;
        this.plan = plan;
        this.pickUp = pickUp;
        this.category = category;
        this.organizator = organizator;
        this.reviews = reviews;
    }
    public Organizator getOrganizator() {
        return organizator;
    }

    public void setOrganizator(Organizator organizator) {
        this.organizator = organizator;
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
