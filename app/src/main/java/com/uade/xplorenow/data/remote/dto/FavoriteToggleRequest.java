package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class FavoriteToggleRequest {

    @SerializedName("activityId")
    private final String activityId;

    public FavoriteToggleRequest(String activityId) {
        this.activityId = activityId;
    }
}
