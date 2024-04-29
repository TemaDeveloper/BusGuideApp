package com.bus_tours_ex.apps.bustours.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reservation {

    @SerializedName("user_id")
    @Expose
    private int userId;

    @SerializedName("trip_id")
    @Expose
    private int tripId;

    @SerializedName("price")
    @Expose
    private int price;

    @SerializedName("num_people")
    @Expose
    private int numPeople;

    @SerializedName("date")
    @Expose
    private String date;

    public Reservation(int userId, int tripId, int price, int numPeople, String date) {
        this.userId = userId;
        this.tripId = tripId;
        this.price = price;
        this.numPeople = numPeople;
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }
}
