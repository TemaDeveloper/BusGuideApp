package com.bus_tours_ex.apps.bustours.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bus_tours_ex.apps.bustours.MainActivity;
import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.admin.AdminPanelActivity;
import com.bus_tours_ex.apps.bustours.managers.SharedPrefManager;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.rest.ApiInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {

    private MaterialButton loginAsGuestButton, loginButton;
    private TextInputEditText emailEditText, passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginAsGuestButton = view.findViewById(R.id.loginAsGuestButton);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLoginForAllUsers();
            }
        });

        loginAsGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        return view;
    }

    private void checkLoginForAllUsers() {
        final String EMAIL = emailEditText.getText().toString().trim();
        final String PASSWORD = passwordEditText.getText().toString().trim();
        if(checkFields(EMAIL, PASSWORD)){
            login(EMAIL, PASSWORD);
        }

    }

    private void login(String EMAIL, String PASSWORD){
        ApiInterface apiInterface = APIClient.getApiService();
        Call<ResponseBody> callLogin = apiInterface.signIn(EMAIL, PASSWORD);
        callLogin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                SharedPrefManager.getInstance(getContext()).setEmail(EMAIL);
                startActivity(new Intent(getContext(), MainActivity.class).putExtra("email", EMAIL));
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("ResponseMessage", t.getMessage() + " message" + " " + t.getCause());
            }
        });
    }

    private boolean checkFields(String email, String password){
        boolean checkFields;
        if(email.isEmpty()){
            emailEditText.setError("Enter your email");
            checkFields = false;
        }else if(password.isEmpty()){
            passwordEditText.setError("Enter your password");
            checkFields = false;
        }else if (email.isEmpty() && password.isEmpty()){
            emailEditText.setError("Enter your email");
            passwordEditText.setError("Enter your password");
            checkFields = false;
        }else{
            checkFields = true;
        }
        return checkFields;
    }


}