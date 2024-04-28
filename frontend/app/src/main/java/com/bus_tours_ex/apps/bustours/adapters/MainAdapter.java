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
import com.bus_tours_ex.apps.bustours.models.Organizator;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.ui.trips.TripDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>{

    private ArrayList<Trip> tripList;
    private Context context;
    private String imageIntent, pickUp, plan, nameManager, telegram, viber, whatsapp, imageManagerIntent;

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

        if (holder.tripImage == null) {
            System.out.println("Trip image holder is null");
        }
        imageIntent = APIClient.DATABASE_URL + item.getImage();
        for(String pickUpPlace : item.getPickUp()){
            pickUp = pickUpPlace;
        }
        plan = item.getPlan();
        Organizator organizator = item.getOrganizator();
        nameManager = organizator.getName();
        telegram = organizator.getTgTag();
        viber = organizator.getViberNumber();
        whatsapp = organizator.getWhatsappNumber();
        imageManagerIntent = APIClient.DATABASE_URL + organizator.getAvatarImg();

        Picasso.get()
                .load(imageIntent)
                .into(holder.tripImage);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void filterList(ArrayList<Trip> filterlist) {
        tripList = filterlist;
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private TextView tripTitle, tripPrice;
        private ImageView tripImage;
        public MainViewHolder(@NonNull View itemView) {
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
                            .putExtra("imageTrip", imageIntent)
                            .putExtra("plan", plan)
                            .putExtra("pickUp", pickUp)
                            .putExtra("nameManager", nameManager)
                            .putExtra("telegram", telegram)
                            .putExtra("viber", viber)
                            .putExtra("whatsapp", whatsapp)
                            .putExtra("managerImage", imageManagerIntent));
                }
            });


        }
    }
}
