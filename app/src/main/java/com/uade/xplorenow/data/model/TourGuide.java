package com.uade.xplorenow.data.model;

import com.google.gson.annotations.SerializedName;

public class TourGuide {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("bio")
    private String bio;

    @SerializedName("photoUrl")
    private String photoUrl;

    @SerializedName("rating")
    private float rating;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getPhotoUrl() { return photoUrl; }
    public float getRating() { return rating; }
}
