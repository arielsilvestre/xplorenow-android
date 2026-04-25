package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateUserRequest {

    @SerializedName("preferences")
    private final List<String> preferences;

    public UpdateUserRequest(List<String> preferences) {
        this.preferences = preferences;
    }
}
