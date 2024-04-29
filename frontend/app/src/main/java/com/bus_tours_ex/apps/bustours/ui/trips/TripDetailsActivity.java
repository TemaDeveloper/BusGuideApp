package com.bus_tours_ex.apps.bustours.ui.trips;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

public class TripDetailsActivity extends AppCompatActivity {

    private ImageView backgroundImage, backImage;
    private TextView titleText, planText, priceText, telegram, viber, whatsapp, managerName, pickUp;
    private MaterialButton reserveButton, reviewButton;
    private ImageView managerImage;
    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        init();

        String tripID = getIntent().getStringExtra("trip_id");

        priceText.setText(getIntent().getStringExtra("priceTrip"));
        titleText.setText(getIntent().getStringExtra("titleTrip"));
        Picasso.get()
                .load(getIntent().getStringExtra("imageTrip"))
                .into(backgroundImage);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println(tripID);
                startActivity(new Intent(getApplicationContext(), BookingTripActivity.class)
                        .putExtra("tripID", tripID)
                        .putExtra("price", priceText.getText().toString()));
            }
        });

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddReviewActivity.class)
                        .putExtra("tripID", tripID));
            }
        });

        getTripInfo();

    }

    private void getTripInfo(){

        Picasso.get()
                .load(APIClient.DATABASE_URL + getIntent().getStringExtra("managerImage"))
                .into(managerImage);
        planText.setText(getIntent().getStringExtra("plan"));
        pickUp.setText(getIntent().getStringExtra("pickUp"));
        managerName.setText(getIntent().getStringExtra("nameManager"));
        telegram.setText(getIntent().getStringExtra("telegram"));
        viber.setText(getIntent().getStringExtra("viber"));
        whatsapp.setText(getIntent().getStringExtra("whatsapp"));

    }

    private void init(){
        priceText = findViewById(R.id.price_text);
        backgroundImage = findViewById(R.id.background_image_view);
        titleText = findViewById(R.id.title_text);
        reserveButton = findViewById(R.id.reserve_button);
        planText = findViewById(R.id.plan_trip_text);
        backImage = findViewById(R.id.back_image_view);
        reviewButton = findViewById(R.id.review_button);

        managerName = findViewById(R.id.manager_name);
        telegram = findViewById(R.id.text_telegram_manager);
        viber = findViewById(R.id.text_viber_manager);
        whatsapp = findViewById(R.id.text_whatsapp_manager);
        managerImage = findViewById(R.id.image_profile_manager);
        pickUp = findViewById(R.id.pick_up_trip_text);
    }

}