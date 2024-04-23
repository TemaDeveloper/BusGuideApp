package com.bus_tours_ex.apps.bustours.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bus_tours_ex.apps.bustours.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    private ImageView backImage;
    private CircleImageView avatarImageView;
    private final int REQUEST_CODE_AVATAR_PHOTO = 200;
    private String TAG = "IMAGE_CHOOSER_E";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        backImage = findViewById(R.id.back_image_view);
        avatarImageView = findViewById(R.id.profileImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGallery();
            }
        });

    }

    private void goToGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_AVATAR_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri == null) return;

                if(requestCode == REQUEST_CODE_AVATAR_PHOTO){
                    avatarImageView.setImageURI(selectedImageUri);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "Selecting picture cancelled");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }

}