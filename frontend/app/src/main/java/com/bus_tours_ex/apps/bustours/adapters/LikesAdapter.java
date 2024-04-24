package com.bus_tours_ex.apps.bustours.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.ui.trips.TripDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.LikesViewHolder>{

    private ArrayList<Trip> trips;
    private Context context;

    public LikesAdapter(ArrayList<Trip> trips, Context context) {
        this.trips = trips;
        this.context = context;
    }

    @NonNull
    @Override
    public LikesAdapter.LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trip_liked_layout, parent, false);
        return new LikesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikesAdapter.LikesViewHolder holder, int position) {
        Trip item = trips.get(position);
        holder.tripTitle.setText(item.getTitle());
        holder.tripPrice.setText(item.getPrice() + " USD");
        Picasso.get().load(item.getImage()).into(holder.tripImage);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class LikesViewHolder extends RecyclerView.ViewHolder {

        private TextView tripTitle, tripPrice;
        private ImageView tripImage;

        public LikesViewHolder(@NonNull View itemView) {
            super(itemView);

            tripPrice = itemView.findViewById(R.id.price_trip_text_view);
            tripTitle = itemView.findViewById(R.id.name_trip_text_view);
            tripImage = itemView.findViewById(R.id.image_trip);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, TripDetailsActivity.class)
                            .putExtra("titleTrip", tripTitle.getText().toString())
                            .putExtra("priceTrip", tripPrice.getText().toString())
                            .putExtra("imageTrip", trips.get(getAdapterPosition()).getImage()));
                }
            });

        }
    }
}
