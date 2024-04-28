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
import com.bus_tours_ex.apps.bustours.models.AuthInfo;
import com.bus_tours_ex.apps.bustours.models.User;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.rest.ApiInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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

        Gson gson = new Gson();
        String json = gson.toJson(new AuthInfo(null, EMAIL, PASSWORD));
        RequestBody authInfo = RequestBody.create(MediaType.parse("application/json"), json);

        Call<ResponseBody> callLogin = apiInterface.signIn(authInfo);
        callLogin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    String jsonString = null;
                    try {
                        jsonString = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonString);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    int userId = 0;
                    boolean isAdmin = false;
                    try {
                        isAdmin = jsonObject.getJSONObject("User").getBoolean("is_admin");
                        userId = jsonObject.getJSONObject("User").getInt("id");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    SharedPrefManager.getInstance(getContext()).setIsAnon(false);
                    SharedPrefManager.getInstance(getContext()).setSavedId(userId);
                    SharedPrefManager.getInstance(getContext()).setIsAdmin(isAdmin);
                    if(isAdmin){
                        startActivity(new Intent(getContext(), AdminPanelActivity.class).putExtra("id", userId));
                    }else{
                        startActivity(new Intent(getContext(), MainActivity.class).putExtra("id", userId));
                    }

                }
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