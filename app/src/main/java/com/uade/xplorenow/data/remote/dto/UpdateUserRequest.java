package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateUserRequest {

    @SerializedName("name")
    private final String name;

    @SerializedName("phone")
    private final String phone;

    @SerializedName("photoUrl")
    private final String photoUrl;

    @SerializedName("preferences")
    private final List<String> preferences;

    public UpdateUserRequest(String name, String phone, String photoUrl, List<String> preferences) {
        this.name = name;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.preferences = preferences;
    }
}
