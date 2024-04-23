package com.bus_tours_ex.apps.bustours.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.auth.AuthActivity;
import com.bus_tours_ex.apps.bustours.databinding.FragmentProfileBinding;
import com.google.android.material.button.MaterialButton;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private MaterialButton loginButton;
    private ImageView editImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loginButton = root.findViewById(R.id.loginButton);
        editImage = root.findViewById(R.id.editImage);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditActivity.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AuthActivity.class));
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}