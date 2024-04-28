package com.bus_tours_ex.apps.bustours.ui.trips;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.adapters.FiltersAdapter;
import com.bus_tours_ex.apps.bustours.adapters.MainAdapter;
import com.bus_tours_ex.apps.bustours.adapters.UpdateTripCategory;
import com.bus_tours_ex.apps.bustours.databinding.FragmentTripsBinding;
import com.bus_tours_ex.apps.bustours.models.FilterItem;
import com.bus_tours_ex.apps.bustours.models.Trip;

import java.util.ArrayList;
import java.util.Random;


public class TripsFragment extends Fragment implements UpdateTripCategory {

    private TextView categoryName, randomTip;
    private FragmentTripsBinding binding;
    private RecyclerView filtersRecyclerView, mainRecyclerView;
    private ArrayList<FilterItem> filters;
    private FiltersAdapter filtersAdapter;
    private MainAdapter mainAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTripsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init(root);

        filters = new ArrayList<>();

        filters.add(new FilterItem(getResources().getString(R.string.newTrips),
                R.drawable.new_tours_img));
        filters.add(new FilterItem(getResources().getString(R.string.popular),
                R.drawable.popular_tours_img));
        filters.add(new FilterItem(getResources().getString(R.string.europe),
                R.drawable.europe_tours_img));
        filters.add(new FilterItem(getResources().getString(R.string.students),
                R.drawable.students_tour_img));

        mainRecyclerView.setHasFixedSize(true);
        filtersRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        //fetchTrips();

        filtersAdapter = new FiltersAdapter(TripsFragment.this::callBack, filters, getContext());
        filtersRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false
                ));
        filtersRecyclerView.setAdapter(filtersAdapter);

        randomTip.setText(getRandomTravelTip(getContext()));

        return root;
    }



    // Method to get a random travel tip.
    public static String getRandomTravelTip(Context context) {
        Resources res = context.getResources();
        String[] tips = res.getStringArray(R.array.bus_travel_tips);
        int randomIndex = new Random().nextInt(tips.length);
        return tips[randomIndex];
    }


    private void init(View root){
        filtersRecyclerView = root.findViewById(R.id.filters_recyclerview);
        mainRecyclerView = root.findViewById(R.id.main_recyclerview);
        categoryName = root.findViewById(R.id.category_name);
        randomTip = root.findViewById(R.id.tipsText);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void callBack(int position, ArrayList<Trip> tripList, String category) {
        mainAdapter = new MainAdapter(tripList, getContext());
        mainAdapter.notifyDataSetChanged();
        mainRecyclerView.setAdapter(mainAdapter);
        categoryName.setText(category);
        System.out.println(category + " ====== > Cat");
    }
}