package com.bus_tours_ex.apps.bustours.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    int id;
    @SerializedName("avatar")
    String avatar;
    @SerializedName("name")
    String name;
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;
    @SerializedName("is_admin")
    boolean isAdmin;

    public User(int id, String avatar, String name, String email, String password, boolean isAdmin) {
        this.avatar = avatar;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.id = id;
    }

    public User(String name, String email, String password, boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
