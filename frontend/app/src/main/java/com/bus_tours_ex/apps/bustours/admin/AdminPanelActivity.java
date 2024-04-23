package com.bus_tours_ex.apps.bustours.admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bus_tours_ex.apps.bustours.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminPanelActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView createAdmin, tourPhoto;
    private final int REQUEST_CODE_TOUR_PHOTO = 200;
    private final int REQUEST_CODE_MANAGER_PHOTO = 201;
    private String TAG = "IMAGE_CHOOSER_E";
    private ImageView tourImage;
    private CircleImageView photoManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        createAdmin = findViewById(R.id.create_admin_text);
        tourPhoto = findViewById(R.id.upload_image_text_view);
        tourImage = findViewById(R.id.tour_image);
        photoManager = findViewById(R.id.photo_manager);

        tourPhoto.setOnClickListener(this::onClick);
        createAdmin.setOnClickListener(this::onClick);
        photoManager.setOnClickListener(this::onClick);

    }

    private void goToGallery(int requestCode){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri == null) return;

                switch (requestCode) {
                    case REQUEST_CODE_TOUR_PHOTO:
                        tourImage.setImageURI(selectedImageUri);
                        tourPhoto.setText("Change Tour Photo");
                        break;
                    case REQUEST_CODE_MANAGER_PHOTO:
                        photoManager.setImageURI(selectedImageUri);
                        break;
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
        if (id == R.id.photo_manager) {
            goToGallery(REQUEST_CODE_MANAGER_PHOTO);
        } else if (id == R.id.create_admin_text) {
            startActivity(new Intent(getApplicationContext(), AdminCreationActivity.class));
        } else if (id == R.id.upload_image_text_view) {
            goToGallery(REQUEST_CODE_TOUR_PHOTO);
        }
    }

}
