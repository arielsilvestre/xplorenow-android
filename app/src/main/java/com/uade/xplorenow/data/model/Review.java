package com.uade.xplorenow.data.model;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("id")
    private String id;

    @SerializedName("activityId")
    private String activityId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("stars")
    private int stars;

    @SerializedName("comment")
    private String comment;

    public String getId() { return id; }
    public String getActivityId() { return activityId; }
    public String getUserId() { return userId; }
    public int getStars() { return stars; }
    public String getComment() { return comment; }
}
