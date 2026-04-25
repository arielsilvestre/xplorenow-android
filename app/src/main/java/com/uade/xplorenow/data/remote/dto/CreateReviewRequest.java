package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CreateReviewRequest {

    @SerializedName("activityId")
    private final String activityId;

    @SerializedName("stars")
    private final int stars;

    @SerializedName("comment")
    private final String comment;

    public CreateReviewRequest(String activityId, int stars, String comment) {
        this.activityId = activityId;
        this.stars = stars;
        this.comment = comment;
    }
}
