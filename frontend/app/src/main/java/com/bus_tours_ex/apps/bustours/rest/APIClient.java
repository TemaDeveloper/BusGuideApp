package com.bus_tours_ex.apps.bustours.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static final String DATABASE_URL = "http://127.0.0.1:3000/";
    public static Retrofit retrofit = null;
    public static Retrofit getRetrofitInstance(){
        if(retrofit == null) {
            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(DATABASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(/*gson*/))
                    //.client(client)
                    .build();
        }
        return retrofit;
    }

    public static ApiInterface getApiService() {
        return getRetrofitInstance().create(ApiInterface.class);
    }




}
