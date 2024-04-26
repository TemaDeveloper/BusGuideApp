package com.bus_tours_ex.apps.bustours.rest;

import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("register/")
    Call<ResponseBody> signUp(@Field("avatar") String avatar,
                              @Field("name") String name,
                              @Field("email") String email,
                              @Field("password") String password,
                              @Field("is_admin") boolean isAdmin);

    @FormUrlEncoded
    @POST("user-auth/")
    Call<ResponseBody> signIn(@Field("email") String email,
                              @Field("password") String password);

    @GET("trip/all")
    Call<List<Trip>> getAllTrips();

    // image, title, price, category, pick_up_points
    @Multipart
    @POST("/trip")
    static Call<Response> createTrip(
            @Part("info") RequestBody info,
            @Part MultipartBody.Part tripPicture,
            @Part MultipartBody.Part organizatorAvatar
    );



}
