package com.bus_tours_ex.apps.bustours.ui.likes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.adapters.LikesAdapter;
import com.bus_tours_ex.apps.bustours.models.Trip;

import java.util.ArrayList;

public class LikeFragment extends Fragment implements OnItemDeleteListener{

    private RecyclerView recyclerView;
    private ArrayList<Trip> likedTrips;
    private LikesAdapter likesAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_like, container, false);

        likedTrips = new ArrayList<>();
        likedTrips.add(new Trip("Egypt", "https://www.rjtravelagency.com/wp-content/uploads/2023/07/Cairo-Egypt.jpg", 70));
        likesAdapter = new LikesAdapter(likedTrips, getContext(), this::onDelete);



        recyclerView = view.findViewById(R.id.likes_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(likesAdapter);

        return view;
    }

    @Override
    public void onDelete(int position) {
        likesAdapter.deleteItem(position);
    }

}