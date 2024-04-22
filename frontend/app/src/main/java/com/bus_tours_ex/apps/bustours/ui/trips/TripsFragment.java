package com.bus_tours_ex.apps.bustours.ui.trips;

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
import com.bus_tours_ex.apps.bustours.databinding.FragmentTripsBinding;
import com.bus_tours_ex.apps.bustours.models.FilterItem;
import com.bus_tours_ex.apps.bustours.models.Trip;

import java.util.ArrayList;

public class TripsFragment extends Fragment implements UpdateTripCategory {

    private TextView categoryName;
    private FragmentTripsBinding binding;
    private RecyclerView filtersRecyclerView, mainRecyclerView;
    private ArrayList<FilterItem> filters;
    private ArrayList<Trip> trips;
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
                R.drawable.student_tours_img));

        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        buildRecyclerView();

        filtersAdapter = new FiltersAdapter(TripsFragment.this::callBack, filters, getContext());
        filtersRecyclerView.hasFixedSize();
        filtersRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false
                ));
        filtersRecyclerView.setAdapter(filtersAdapter);



        return root;
    }

    private void buildRecyclerView(){
        trips = new ArrayList<>();

        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
        trips.add(new Trip("The Trip on Paris",
                "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg",
                120));
    }

    private void init(View root){
        filtersRecyclerView = root.findViewById(R.id.filters_recyclerview);
        mainRecyclerView = root.findViewById(R.id.main_recyclerview);
        categoryName = root.findViewById(R.id.category_name);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void callBack(int position, ArrayList<Trip> foodList, String category) {
        mainAdapter = new MainAdapter(foodList, getContext());
        mainAdapter.notifyDataSetChanged();
        mainRecyclerView.setAdapter(mainAdapter);
        categoryName.setText(category);
    }
}