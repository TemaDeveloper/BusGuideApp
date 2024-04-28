package com.bus_tours_ex.apps.bustours.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.adapters.MainAdapter;
import com.bus_tours_ex.apps.bustours.databinding.FragmentSearchBinding;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.rest.AllTripResponse;
import com.bus_tours_ex.apps.bustours.rest.ApiInterface;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment {
    private ArrayList<Trip> trips;
    private MainAdapter adapter;
    private RecyclerView tripsRecyclerView;
    private FragmentSearchBinding binding;
    private TextInputEditText searchEditText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tripsRecyclerView = root.findViewById(R.id.search_recyclerView);
        searchEditText = root.findViewById(R.id.searchEditText);
        tripsRecyclerView.setHasFixedSize(true);
        tripsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        trips = new ArrayList<>();
        fetchTrips();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


        return root;
    }

    private void fetchTrips(){
        ApiInterface apiInterface = APIClient.getApiService();
        Call<AllTripResponse> callIdTrips = apiInterface.getAllTrips();
        callIdTrips.enqueue(new Callback<AllTripResponse>() {
            @Override
            public void onResponse(Call<AllTripResponse> call, Response<AllTripResponse> response) {
                if (response.isSuccessful()) {
                    for (Integer id : response.body().getIds()) {
                        apiInterface.getTrip(id).enqueue(new Callback<Trip>() {
                                    @Override
                                    public void onResponse(Call<Trip> call, Response<Trip> response) {
                                        synchronized (trips){
                                            trips.add(response.body());
                                            adapter = new MainAdapter(trips, getContext());
                                            tripsRecyclerView.setAdapter(adapter);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Trip> call, Throwable throwable) {

                                    }
                                });
                    }


                }
            }

            @Override
            public void onFailure(Call<AllTripResponse> call, Throwable t) {
                Log.d("FAIL_GETTING_U", t.getMessage());
            }
        });
    }

    private void filter(String text) {
        ArrayList<Trip> filteredlist = new ArrayList<Trip>();
        for (Trip item : trips) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            adapter.filterList(filteredlist);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}