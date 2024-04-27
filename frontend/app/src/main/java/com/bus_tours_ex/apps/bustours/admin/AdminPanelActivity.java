package com.bus_tours_ex.apps.bustours.admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.auth.AuthActivity;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.rest.ApiInterface;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanelActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView createAdmin, tourPhoto, logoutText;
    private final int REQUEST_CODE_TOUR_PHOTO = 200;
    private String TAG = "IMAGE_CHOOSER_E";
    private ImageView tourImage;
    private MaterialButton allReservationsButton;
    private EditText tripTitle, tripPlan, tripPrice, tripPickUp;
    private Spinner categoriesSpinner;
    private String[] categories;
    private String chosenCategory;
    private InputStream iStream;
    private final int REQUEST_CODE_MANAGER_PHOTO = 201;
    private CircleImageView photoManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        init();
        createSpinner();

        tourPhoto.setOnClickListener(this::onClick);
        createAdmin.setOnClickListener(this::onClick);
        allReservationsButton.setOnClickListener(this::onClick);
        logoutText.setOnClickListener(this::onClick);
        photoManager.setOnClickListener(this::onClick);

    }

    private void createSpinner(){
        //Getting the instance of Spinner and applying OnItemSelectedListener on it

        categoriesSpinner.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, categories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        categoriesSpinner.setAdapter(aa);
    }


    private void create() throws IOException {

        String title = tripTitle.getText().toString();
        int price = Integer.valueOf(tripPrice.getText().toString());
        String pickUp = tripPickUp.getText().toString();
        String plan = tripPlan.getText().toString();

        Gson gson = new Gson();
        String json = gson.toJson(new Trip(title, price, plan, pickUp, chosenCategory));
        RequestBody info = RequestBody.create(MediaType.parse("application/json"), json);

        byte[] inputData = getBytes(iStream);

        File tripPictureFile = new File(Arrays.toString(inputData));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), tripPictureFile);
        MultipartBody.Part tripPicture = MultipartBody.Part.createFormData("image", tripPictureFile.getName(), requestFile);

        File avatarFile = new File(Arrays.toString(inputData));
        RequestBody avatarRequestFile = RequestBody.create(MediaType.parse("image/png"), avatarFile);
        MultipartBody.Part organizatorAvatar = MultipartBody.Part.createFormData("avatar_img", avatarFile.getName(), avatarRequestFile);

        Call<Trip> call = APIClient.getApiService().createTrip(info, tripPicture, organizatorAvatar);

        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, retrofit2.Response<Trip> response) {
                if (response.isSuccessful()) {
                    // Handle success
                    Log.d("TripCreation", "Trip created successfully with ID: " + response.body().toString());
                } else {
                    Log.e("TripCreation", "Failed to create trip: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.e("TripCreation", "Error calling API", t);
            }
        });

    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void init(){

        categories = getResources().getStringArray(R.array.categories);

        tripPlan = findViewById(R.id.tour_plan_admin);
        tripTitle = findViewById(R.id.tour_naming_admin);
        tripPrice = findViewById(R.id.tour_price_admin);
        tripPickUp = findViewById(R.id.tour_pick_up_admin);
        categoriesSpinner = findViewById(R.id.spinner_categories);
        photoManager = findViewById(R.id.photo_manager);

        createAdmin = findViewById(R.id.create_admin_text);
        tourPhoto = findViewById(R.id.upload_image_text_view);
        tourImage = findViewById(R.id.tour_image);

        allReservationsButton = findViewById(R.id.all_reservations_button);
        logoutText = findViewById(R.id.log_out_button);

    }

    private void goToGallery(int request){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImageUri = data.getData();
                iStream = getContentResolver().openInputStream(selectedImageUri);

                if (selectedImageUri == null) return;

                if(REQUEST_CODE_TOUR_PHOTO == requestCode){
                    tourImage.setImageURI(selectedImageUri);
                    tourPhoto.setText("Change Tour Photo");
                }else if(REQUEST_CODE_MANAGER_PHOTO == requestCode){
                    photoManager.setImageURI(selectedImageUri);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "Selecting picture cancelled");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.create_admin_text) {
            startActivity(new Intent(getApplicationContext(), AdminCreationActivity.class));
        }else if (id == R.id.photo_manager) {
            goToGallery(REQUEST_CODE_MANAGER_PHOTO);
        }else if (id == R.id.upload_image_text_view) {
            goToGallery(REQUEST_CODE_TOUR_PHOTO);
        }else if (id == R.id.all_reservations_button) {
            startActivity(new Intent(getApplicationContext(), AllReservationsActivity.class));
        }else if (id == R.id.log_out_button) {
            startActivity(new Intent(getApplicationContext(), AuthActivity.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenCategory = categories[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
