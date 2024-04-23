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

import com.airbnb.lottie.LottieAnimationView;
import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.models.FilterItem;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.ui.trips.TripDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>{

    private ArrayList<Trip> tripList;
    private Context context;

    public void filterList(ArrayList<Trip> filterlist) {
        tripList = filterlist;
        notifyDataSetChanged();
    }

    public MainAdapter(ArrayList<Trip> tripList, Context context) {
        this.tripList = tripList;
        this.context = context;
    }

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trip_layout, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MainViewHolder holder, int position) {
        Trip item = tripList.get(position);
        holder.tripTitle.setText(item.getTitle());
        holder.tripPrice.setText(item.getPrice() + " USD");
        Picasso.get().load(item.getImage()).into(holder.tripImage);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private TextView tripTitle, tripPrice;
        private ImageView tripImage;
        private LottieAnimationView likeTripAnimation;
        private boolean isChecked = false;
        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            likeTripAnimation = itemView.findViewById(R.id.likeLottieAnimation);
            tripPrice = itemView.findViewById(R.id.price_trip_text_view);
            tripTitle = itemView.findViewById(R.id.name_trip_text_view);
            tripImage = itemView.findViewById(R.id.image_trip);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, TripDetailsActivity.class)
                            .putExtra("titleTrip", tripTitle.getText().toString())
                            .putExtra("priceTrip", tripPrice.getText().toString())
                            .putExtra("imageTrip", tripList.get(getAdapterPosition()).getImage()));
                }
            });

            likeTripAnimation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isChecked){
                        likeTripAnimation.setSpeed(-1);
                        likeTripAnimation.playAnimation();
                        isChecked = false;
                    }else{
                        likeTripAnimation.setSpeed(1);
                        likeTripAnimation.playAnimation();
                        isChecked = true;
                    }
                }
            });

        }
    }
}
