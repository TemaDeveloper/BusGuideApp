package com.bus_tours_ex.apps.bustours.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.adapters.MainAdapter;
import com.bus_tours_ex.apps.bustours.auth.AuthActivity;
import com.bus_tours_ex.apps.bustours.databinding.FragmentProfileBinding;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private MaterialButton loginButton;
    private ImageView editImage;
    private RecyclerView myReservationsRecyclerView;
    private ArrayList<Trip> trips;
    private MainAdapter adapterMyReservations;
    private LinearLayout linReservations;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loginButton = root.findViewById(R.id.loginButton);
        editImage = root.findViewById(R.id.editImage);
        myReservationsRecyclerView = root.findViewById(R.id.my_reservations_recycler_view);
        linReservations = root.findViewById(R.id.lin_reservations);

        trips = new ArrayList<>();
        trips.add(new Trip("Trip to China", "https://media.cnn.com/api/v1/images/stellar/prod/230529151056-aerial-wuhan-china.jpg?c=original", 80));
        trips.add(new Trip("Trip to China", "https://media.cnn.com/api/v1/images/stellar/prod/230529151056-aerial-wuhan-china.jpg?c=original", 80));
        trips.add(new Trip("Trip to China", "https://media.cnn.com/api/v1/images/stellar/prod/230529151056-aerial-wuhan-china.jpg?c=original", 80));

        adapterMyReservations = new MainAdapter(trips, getContext());
        myReservationsRecyclerView.setHasFixedSize(true);
        myReservationsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        myReservationsRecyclerView.setAdapter(adapterMyReservations);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditActivity.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AuthActivity.class));
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}