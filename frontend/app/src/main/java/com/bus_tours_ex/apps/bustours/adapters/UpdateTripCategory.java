package com.bus_tours_ex.apps.bustours.adapters;

import com.bus_tours_ex.apps.bustours.models.Trip;

import java.util.ArrayList;

public interface UpdateTripCategory {
    void callBack(int id, int position, ArrayList<Trip> tripList, String category);
}
