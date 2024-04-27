package com.bus_tours_ex.apps.bustours.rest;

import androidx.annotation.Nullable;

import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("organizator/{id}/avatar")
    Call<ResponseBody> getOrganizatorAvatar(@Path("id") int id);

    @Multipart
    @POST("register")
    Call<User> registerUser(@Part("info") RequestBody userInfo, @Part MultipartBody.Part avatar);

    @FormUrlEncoded
    @GET("user-auth")
    Call<ResponseBody> signIn(@Field("email") String email,
                              @Field("password") String password);

    @GET("user/{id}")
    Call<User> getUser(@Path("id") int id);

    @GET("user/{id}/avatar")
    Call<ResponseBody> getUserAvatar(@Path("id") int id);

    @Multipart
    @PATCH("user/{id}/avatar")
    Call<ResponseBody> updateUserAvatar(@Path("id") int id, @Part MultipartBody.Part avatar);

    @Multipart
    @POST("trip")
    Call<Trip> createTrip(@Part("info") RequestBody tripInfo, @Part MultipartBody.Part image, @Part MultipartBody.Part avatar);

    @GET("trip/{id}")
    Call<Trip> getTrip(@Path("id") int id);

    @GET("trip/all")
    Call<List<Trip>> getAllTrips();

    @GET("trip/{id}/image")
    Call<ResponseBody> getTripImage(@Path("id") int id);

}