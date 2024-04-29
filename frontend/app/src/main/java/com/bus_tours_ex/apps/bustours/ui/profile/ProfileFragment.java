package com.bus_tours_ex.apps.bustours.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.adapters.MainAdapter;
import com.bus_tours_ex.apps.bustours.auth.AuthActivity;
import com.bus_tours_ex.apps.bustours.databinding.FragmentProfileBinding;
import com.bus_tours_ex.apps.bustours.managers.SharedPrefManager;
import com.bus_tours_ex.apps.bustours.models.ReservationList;
import com.bus_tours_ex.apps.bustours.models.ResponseWrapper;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.models.User;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.rest.ApiInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private MaterialButton loginButton, logOutButton;
    private MaterialCardView cardLogin;
    private RecyclerView myReservationsRecyclerView;
    private ArrayList<Trip> trips;
    private MainAdapter adapterMyReservations;
    private TextView nameText, emailText, updateAvatarImage;
    private LinearLayout linReservations;
    private CircleImageView avatarImageView;
    private final int REQUEST_CODE_AVATAR_PHOTO = 200;
    private String TAG = "IMAGE_CHOOSER_E";
    private byte[] avatarBytes;
    private int ID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ID = SharedPrefManager.getInstance(getContext()).getSavedId();
        loginButton = root.findViewById(R.id.loginButton);
        myReservationsRecyclerView = root.findViewById(R.id.my_reservations_recycler_view);
        linReservations = root.findViewById(R.id.lin_reservations);
        avatarImageView = root.findViewById(R.id.profileImage);
        nameText = root.findViewById(R.id.nameText);
        emailText = root.findViewById(R.id.emailText);
        cardLogin = root.findViewById(R.id.cardLogin);
        logOutButton = root.findViewById(R.id.log_out_button);
        updateAvatarImage = root.findViewById(R.id.update_avatar_image);

        trips = new ArrayList<>();
        myReservationsRecyclerView.setHasFixedSize(true);
        myReservationsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        fetchReservations();
        updateAvatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage();
                updateAvatarImage.setVisibility(View.GONE);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getContext()).setIsAnon(true);
                startActivity(new Intent(getContext(), AuthActivity.class));
            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGallery();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AuthActivity.class));
            }
        });

        if(SharedPrefManager.getInstance(getContext()).getIsAnon() == false){
            getUserData();
            linReservations.setVisibility(View.VISIBLE);
            cardLogin.setVisibility(View.GONE);
        }else{
            linReservations.setVisibility(View.GONE);
            cardLogin.setVisibility(View.VISIBLE);
        }


        return root;
    }

    private void updateImage(){
        // Convert trip and organizer data to JSON

        Call<ResponseBody> call = APIClient.getApiService().updateUserAvatar(ID, toMultipartBodyPart("avatar", avatarBytes, "avatar"));
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

    private void fetchReservations(){
        ApiInterface apiInterface = APIClient.getApiService();
        Call<ReservationList> callReservations = apiInterface.getReservations(ID);
        callReservations.enqueue(new Callback<ReservationList>() {
            @Override
            public void onResponse(Call<ReservationList> call, Response<ReservationList> response) {

                if (response.isSuccessful()) {

                    for(int i = 0; i < response.body().getReservations().size(); i++){
                        int tripID = response.body().getReservations().get(i).getTripId();
                        System.out.println("trip_ID " + tripID);
                        apiInterface.getTrip(tripID).enqueue(new Callback<Trip>() {
                            @Override
                            public void onResponse(Call<Trip> call, Response<Trip> response) {
                                synchronized (trips) {
                                    trips.add(response.body());
                                    adapterMyReservations = new MainAdapter(tripID, trips, getContext());
                                    myReservationsRecyclerView.setAdapter(adapterMyReservations);
                                }
                            }

                            @Override
                            public void onFailure(Call<Trip> call, Throwable throwable) {

                            }
                        });
                    }


                }
            }

            @Override
            public void onFailure(Call<ReservationList> call, Throwable t) {
                Log.d("FAIL_GETTING_U", t.getMessage());
            }
        });
    }

    private void getUserData(){

        ApiInterface apiInterface = APIClient.getApiService();
        int ID = SharedPrefManager.getInstance(getContext()).getSavedId();
        Call<ResponseWrapper> callRegister = apiInterface.getUser(ID);
        callRegister.enqueue(new Callback<ResponseWrapper>() {
            @Override
            public void onResponse(Call<ResponseWrapper> call, Response<ResponseWrapper> response) {
                if (response.isSuccessful()) {
                    User user = response.body().getUser();

                    String userName = user.getName();
                    String userEmail = user.getEmail();
                    String userAvatar = user.getAvatar();

                    nameText.setText(userName);
                    emailText.setText(userEmail);
                    Picasso.get().load(userAvatar).into(avatarImageView);

                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper> call, Throwable t) {
                Log.d("FAIL_GETTING_U", t.getMessage());
            }
        });

    }

    private void goToGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_AVATAR_PHOTO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK && data != null) {

                Uri selectedImageUri = data.getData();
                InputStream stream = getContext().getContentResolver().openInputStream(selectedImageUri);
                byte[] bytes = getBytes(stream);
                if (selectedImageUri == null) return;

                if(requestCode == REQUEST_CODE_AVATAR_PHOTO){
                    avatarImageView.setImageURI(selectedImageUri);
                    avatarBytes = bytes;
                    updateAvatarImage.setVisibility(View.VISIBLE);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "Selecting picture cancelled");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}