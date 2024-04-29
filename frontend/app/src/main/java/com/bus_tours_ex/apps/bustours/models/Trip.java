package com.bus_tours_ex.apps.bustours.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trip {


    @SerializedName("trip_id")
    @Expose
    private int tripId;
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
    private Review[] reviews;

    public Trip(String title, int price, String plan, String[] pickUp, String category, Organizator organizator, Review[] reviews) {
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

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getTripId() {
        return tripId;
    }

    public String getPlan() {
        return plan;
    }

    public String[] getPickUp() {
        return pickUp;
    }

    public String getCategory() {
        return category;
    }

    public Review[] getReviews() {
        return reviews;
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
