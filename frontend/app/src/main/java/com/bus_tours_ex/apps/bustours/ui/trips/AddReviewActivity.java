package com.bus_tours_ex.apps.bustours.ui.trips;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bus_tours_ex.apps.bustours.MainActivity;
import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.managers.SharedPrefManager;
import com.bus_tours_ex.apps.bustours.models.Review;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.rest.ApiInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReviewActivity extends AppCompatActivity {

    private MaterialButton submitButton;
    private ImageView imageBack;
    private TextInputEditText descriptionEditText;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        //rating, description, trip_id, user_id

        ratingBar = findViewById(R.id.rating_bar);
        imageBack = findViewById(R.id.image_back);
        submitButton = findViewById(R.id.submitButton);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReview();
            }
        });

    }

    private void postReview(){
        //rating, description, trip_id, user_id
        ApiInterface apiInterface = APIClient.getApiService();

        int tripId = Integer.parseInt(getIntent().getStringExtra("tripID"));

        Gson gson = new Gson();
        String json = gson.toJson(new Review(ratingBar.getNumStars(),
                descriptionEditText.getText().toString(),
                tripId,
                SharedPrefManager.getInstance(getApplicationContext()).getSavedId()));
        RequestBody reviewInfo = RequestBody.create(MediaType.parse("application/json"), json);

        Call<ResponseBody> callPostReview = apiInterface.makeReview(tripId, reviewInfo);
        callPostReview.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast.makeText(AddReviewActivity.this, "success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }

}