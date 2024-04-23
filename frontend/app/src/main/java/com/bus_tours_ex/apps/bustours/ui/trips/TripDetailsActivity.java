package com.bus_tours_ex.apps.bustours.ui.trips;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bus_tours_ex.apps.bustours.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

public class TripDetailsActivity extends AppCompatActivity {

    private ImageView backgroundImage, backImage;
    private TextView titleText, planText, priceText;
    private MaterialButton reserveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        init();

        priceText.setText(getIntent().getStringExtra("priceTrip"));
        titleText.setText(getIntent().getStringExtra("titleTrip"));
        Picasso.get().load(getIntent().getStringExtra("imageTrip")).into(backgroundImage);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BookingTripActivity.class));
            }
        });

    }

    private void init(){
        priceText = findViewById(R.id.price_text);
        backgroundImage = findViewById(R.id.background_image_view);
        titleText = findViewById(R.id.title_text);
        reserveButton = findViewById(R.id.reserve_button);
        planText = findViewById(R.id.plan_trip_text);
        backImage = findViewById(R.id.back_image_view);
    }

}