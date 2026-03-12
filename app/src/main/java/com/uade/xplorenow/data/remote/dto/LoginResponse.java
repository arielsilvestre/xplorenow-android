package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import com.uade.xplorenow.data.model.User;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private User user;

    public String getToken() { return token; }
    public User getUser() { return user; }
}
