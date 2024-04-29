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

import androidx.appcompat.app.AppCompatActivity;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.auth.AuthActivity;
import com.bus_tours_ex.apps.bustours.managers.SharedPrefManager;
import com.bus_tours_ex.apps.bustours.models.Organizator;
import com.bus_tours_ex.apps.bustours.models.Review;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AdminPanelActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView tourPhoto, logoutText;
    private final int REQUEST_CODE_TOUR_PHOTO = 200;
    private String TAG = "IMAGE_CHOOSER_E";
    private ImageView tourImage;
    private MaterialButton allReservationsButton, createTripButton;
    private EditText tripTitle, tripPlan, tripPrice, tripPickUp, managerName, managerEmail, managerTeleg, managerViber, managerWatsApp;
    private Spinner categoriesSpinner;
    private String[] categories;
    private String chosenCategory;
    private final int REQUEST_CODE_MANAGER_PHOTO = 201;
    private CircleImageView photoManager;
    private byte[] tourImageBytes;
    private byte[] managerImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        init();
        createSpinner();

        tourPhoto.setOnClickListener(this::onClick);
        allReservationsButton.setOnClickListener(this::onClick);
        logoutText.setOnClickListener(this::onClick);
        photoManager.setOnClickListener(this::onClick);
        createTripButton.setOnClickListener(this::onClick);

    }

    private void createSpinner() {
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        categoriesSpinner.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        categoriesSpinner.setAdapter(aa);
    }


    private void create() throws IOException {

        String title = tripTitle.getText().toString();
        int price = Integer.valueOf(tripPrice.getText().toString());
        String pickUp = tripPickUp.getText().toString();
        String plan = tripPlan.getText().toString();

        String emailM = managerEmail.getText().toString();
        String nameM = managerName.getText().toString();
        String telegramM = managerTeleg.getText().toString();
        String viberM = managerViber.getText().toString();
        String watsAppM = managerWatsApp.getText().toString();

        // Convert trip and organizer data to JSON
        Gson gson = new Gson();
        String json = gson.toJson(new Trip(title, price, plan, new String[] {pickUp}, chosenCategory, new Organizator(nameM, "", "", emailM, watsAppM, telegramM, viberM), new Review[]{}));
        RequestBody info = RequestBody.create(MediaType.parse("application/json"), json);

        // Send the multipart request with Retrofi't
        Call<ResponseBody> call = APIClient.getApiService().createTrip(info, toMultipartBodyPart("trip_picture", tourImageBytes, "trip_picture"),
                toMultipartBodyPart("organizator_avatar", managerImageBytes, "organizator_avatar"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success
                    Log.d("TripCreation", "Trip created successfully with ID: " + response.body().toString());
                } else {
                    Log.e("TripCreation", "Failed to create trip: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TripCreation", "Error calling API", t);
            }
        });

    }

    public static MultipartBody.Part toMultipartBodyPart(String partName, byte[] byteArray, String fileName) {
        // Create a RequestBody from the byte array
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), byteArray);
        return MultipartBody.Part.createFormData(partName, fileName, requestBody);
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

    private void init() {

        categories = getResources().getStringArray(R.array.categories);

        tripPlan = findViewById(R.id.tour_plan_admin);
        tripTitle = findViewById(R.id.tour_naming_admin);
        tripPrice = findViewById(R.id.tour_price_admin);
        tripPickUp = findViewById(R.id.tour_pick_up_admin);
        categoriesSpinner = findViewById(R.id.spinner_categories);
        photoManager = findViewById(R.id.photo_manager);
        createTripButton = findViewById(R.id.create_trip_button);

        managerName = findViewById(R.id.manager_name_admin);
        managerEmail = findViewById(R.id.manager_email_admin);
        managerTeleg = findViewById(R.id.telegram_nickname_manager);
        managerViber = findViewById(R.id.viber_manager);
        managerWatsApp = findViewById(R.id.watsapp_manager);

        tourPhoto = findViewById(R.id.upload_image_text_view);
        tourImage = findViewById(R.id.tour_image);

        allReservationsButton = findViewById(R.id.all_reservations_button);
        logoutText = findViewById(R.id.log_out_button);

    }

    private void goToGallery(int request) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri == null) return;

            try {
                InputStream stream = getContentResolver().openInputStream(selectedImageUri);
                byte[] bytes = getBytes(stream);  // Ensure this method closes the InputStream

                if (requestCode == REQUEST_CODE_TOUR_PHOTO) {
                    tourImage.setImageURI(selectedImageUri);
                    tourPhoto.setText("Change Tour Photo");
                    tourImageBytes = bytes;  // Save the byte array
                } else if (requestCode == REQUEST_CODE_MANAGER_PHOTO) {
                    photoManager.setImageURI(selectedImageUri);
                    managerImageBytes = bytes;  // Save the byte array
                }
            } catch (IOException e) {
                Log.e(TAG, "Error processing image input: " + e.getMessage());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e(TAG, "Selecting picture cancelled");
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.photo_manager) {
            goToGallery(REQUEST_CODE_MANAGER_PHOTO);
        } else if (id == R.id.upload_image_text_view) {
            goToGallery(REQUEST_CODE_TOUR_PHOTO);
        } else if (id == R.id.all_reservations_button) {
            startActivity(new Intent(getApplicationContext(), AllReservationsActivity.class));
        } else if (id == R.id.log_out_button) {
            SharedPrefManager.getInstance(getApplicationContext()).setIsAnon(true);
            SharedPrefManager.getInstance(getApplicationContext()).setIsAdmin(false);
            startActivity(new Intent(getApplicationContext(), AuthActivity.class));
        } else if (id == R.id.create_trip_button) {
            try {
                create();
                onResume();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenCategory = categories[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
