package com.bus_tours_ex.apps.bustours.ui.trips;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bus_tours_ex.apps.bustours.MainActivity;
import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.managers.SharedPrefManager;
import com.bus_tours_ex.apps.bustours.models.AuthInfo;
import com.bus_tours_ex.apps.bustours.models.Reservation;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.rest.ApiInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingTripActivity extends AppCompatActivity {

    private TextView calendarDates, priceTourTotal;
    private ImageView backImageView;
    private TextInputEditText peopleNumberEditText;
    private MaterialButton bookButton;
    private int tripID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_trip);

        calendarDates = findViewById(R.id.calendar_range_text);
        bookButton = findViewById(R.id.bookButton);
        backImageView = findViewById(R.id.back_image_view);
        priceTourTotal = findViewById(R.id.price_tour_total);
        peopleNumberEditText = findViewById(R.id.peopleNumberEditText);
        tripID = Integer.parseInt(getIntent().getStringExtra("tripID"));

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                book();
            }
        });

        peopleNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is intentionally empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ensure UI update is on the main thread
                priceTourTotal.post(new Runnable() {
                    @Override
                    public void run() {
                        priceTourTotal.setText(updatePrice() + " USD");
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is intentionally empty
            }
        });



        calendarDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePicker();
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private int updatePrice() {
        int totalPrice = 0;
        int peopleNo = 0;

        // Safely retrieve and parse the price
        String priceStr = getIntent().getStringExtra("price");
        if (priceStr != null && !priceStr.isEmpty()) {
            try {
                totalPrice = Integer.parseInt(priceStr.replace(" USD", ""));
            } catch (NumberFormatException e) {
                Log.e("updatePrice", "Error parsing price: " + priceStr);
                totalPrice = 0; // Default or error handling
            }
        }

        // Safely retrieve and parse the number of people
        String peopleNoStr = peopleNumberEditText.getText().toString();
        if (!peopleNoStr.isEmpty()) {
            try {
                peopleNo = Integer.parseInt(peopleNoStr);
            } catch (NumberFormatException e) {
                Log.e("updatePrice", "Error parsing number of people: " + peopleNoStr);
                peopleNo = 0; // Default or error handling
            }
        }

        return totalPrice * peopleNo;
    }

    private void book(){
        ApiInterface apiInterface = APIClient.getApiService();

        Gson gson = new Gson();
        String json = gson.toJson(new Reservation(
                SharedPrefManager.getInstance(getApplicationContext()).getSavedId(),
                tripID, updatePrice(), Integer.parseInt(peopleNumberEditText.getText().toString()), calendarDates.getText().toString()));
        RequestBody reserveInfo = RequestBody.create(MediaType.parse("application/json"), json);

        Call<ResponseBody> callBook = apiInterface.book(reserveInfo);
        callBook.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast.makeText(BookingTripActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }

    private void showDateRangePicker() {
        // Define the date validator to allow picking dates from today onwards
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());

        // Material Date Picker for selecting a date range
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select dates");
        builder.setCalendarConstraints(constraintBuilder.build());
        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

        picker.show(getSupportFragmentManager(), picker.toString());
        picker.addOnPositiveButtonClickListener(selection -> {
            // Update the UI when date range is selected
            calendarDates.setText("Start: " + picker.getHeaderText());
        });
    }

}