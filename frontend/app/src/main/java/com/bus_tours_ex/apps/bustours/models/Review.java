package com.bus_tours_ex.apps.bustours.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("rating")
    @Expose
    private int rating;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("user_id")
    @Expose
    int userId;

    @SerializedName("trip_id")
    @Expose
    int tripId;

    //rating, description, trip_id, user_id


    public Review(int rating, String description, int tripId, int userId) {
        this.rating = rating;
        this.description = description;
        this.userId = userId;
        this.tripId = tripId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public int getUserId() {
        return userId;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


}
