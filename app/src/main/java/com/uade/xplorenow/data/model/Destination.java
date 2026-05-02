package com.uade.xplorenow.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Destination {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("activities")
    private List<TourActivity> activities;

    public String getId()                     { return id; }
    public String getName()                   { return name; }
    public String getDescription()            { return description; }
    public String getImageUrl()               { return imageUrl; }
    public Double getLatitude()               { return latitude; }
    public Double getLongitude()              { return longitude; }
    public List<TourActivity> getActivities() { return activities; }
}
