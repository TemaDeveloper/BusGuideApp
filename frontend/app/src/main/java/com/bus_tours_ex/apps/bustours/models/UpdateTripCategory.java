package com.bus_tours_ex.apps.bustours.ui.trips;

import com.bus_tours_ex.apps.bustours.models.Trip;

import java.util.ArrayList;

public interface UpdateTripCategory {
    void callBack(int position, ArrayList<Trip> foodList, String category);
}
