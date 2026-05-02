package com.uade.xplorenow.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoritesData {

    @SerializedName("activities")
    private List<TourActivity> activities;

    @SerializedName("destinations")
    private List<Destination> destinations;

    public List<TourActivity> getActivities() { return activities; }
    public List<Destination> getDestinations() { return destinations; }
}
