package com.bus_tours_ex.apps.bustours.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllTripResponse {
    @SerializedName("trips_ids")
    @Expose
    private List<Integer> ids;

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public AllTripResponse(List<Integer> ids) {
        this.ids = ids;
    }
}
