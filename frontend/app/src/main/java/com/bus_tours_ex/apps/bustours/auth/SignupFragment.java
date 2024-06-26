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
import com.bus_tours_ex.apps.bustours.managers.SharedPrefManager;
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.models.User;
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.rest.ApiInterface;
import com.google.android.material.button.MaterialButton;
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


public class SignupFragment extends Fragment {

    private TextInputEditText nameEditText, emailEditText, passwordEditText;
    private MaterialButton registerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        passwordEditText = view.findViewById(R.id.passwordEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        registerButton = view.findViewById(R.id.signUpButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        return view;
    }

    private void signUp() {

        ApiInterface apiInterface = APIClient.getApiService();
        String email = emailEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Gson gson = new Gson();
        String json = gson.toJson(new User(name, email, password, false));
        RequestBody userInfo = RequestBody.create(MediaType.parse("application/json"), json);

        Call<ResponseBody> callRegister = apiInterface.registerUser(userInfo, null);
        callRegister.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    String jsonString = null;
                    try {
                        jsonString = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println(jsonString);
                    int id = Integer.parseInt(jsonString);
                    SharedPrefManager.getInstance(getContext()).setSavedId(id);
                    SharedPrefManager.getInstance(getContext()).setIsAnon(false);
                    startActivity(new Intent(getContext(), MainActivity.class).putExtra("id", id));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("FAIL_REGISTER", t.getMessage());
            }
        });


    }

}