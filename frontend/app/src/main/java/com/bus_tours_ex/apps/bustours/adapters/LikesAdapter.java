package com.bus_tours_ex.apps.bustours.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.ui.likes.OnItemDeleteListener;
import com.bus_tours_ex.apps.bustours.ui.trips.TripDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.LikesViewHolder>{

    private ArrayList<Trip> trips;
    private Context context;
    private OnItemDeleteListener listener;

    public LikesAdapter(ArrayList<Trip> trips, Context context, OnItemDeleteListener listener) {
        this.trips = trips;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LikesAdapter.LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trip_liked_layout, parent, false);
        return new LikesViewHolder(view, listener);
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

    public void deleteItem(int position) {
        trips.remove(position);
        notifyItemRemoved(position);
    }

    public class LikesViewHolder extends RecyclerView.ViewHolder {

        private TextView tripTitle, tripPrice;
        private ImageView tripImage;
        private LottieAnimationView likeTripAnimation;
        private boolean isChecked = false;

        public LikesViewHolder(@NonNull View itemView, OnItemDeleteListener listener) {
            super(itemView);

            likeTripAnimation = itemView.findViewById(R.id.likeLottieAnimation);
            tripPrice = itemView.findViewById(R.id.price_trip_text_view);
            tripTitle = itemView.findViewById(R.id.name_trip_text_view);
            tripImage = itemView.findViewById(R.id.image_trip);
            likeTripAnimation = itemView.findViewById(R.id.likeLottieAnimation);

            likeTripAnimation.setProgress(1.0f);

            likeTripAnimation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isChecked){
                        likeTripAnimation.setSpeed(1);
                        likeTripAnimation.playAnimation();
                        isChecked = false;
                    }else{
                        likeTripAnimation.setSpeed(-1);
                        likeTripAnimation.playAnimation();
                        isChecked = true;
                        showPopupDialog(listener);
                    }
                }
            });

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

        private void showPopupDialog(OnItemDeleteListener listener) {
            // Create an AlertDialog.Builder instance
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.pop_up_title));
            builder.setMessage(context.getResources().getString(R.string.pop_up_description));

            // Adding positive button
            builder.setPositiveButton(context.getResources().getString(R.string.pop_up_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Handle the OK button click event
                    listener.onDelete(getAdapterPosition());
                }
            });

            // Adding negative button
            builder.setNegativeButton(context.getResources().getString(R.string.pop_up_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Handle the Cancel button click event
                    dialogInterface.dismiss();
                    likeTripAnimation.setSpeed(1);
                    likeTripAnimation.playAnimation();
                    isChecked = false;
                }
            });

            // Create and show the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}
