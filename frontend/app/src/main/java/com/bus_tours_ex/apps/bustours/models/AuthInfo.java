package com.bus_tours_ex.apps.bustours.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthInfo {
    @SerializedName("name")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    // Constructor, getters and setters
    public AuthInfo(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}

