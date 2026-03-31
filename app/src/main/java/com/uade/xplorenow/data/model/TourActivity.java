package com.uade.xplorenow.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de actividad turística.
 * Renombrado TourActivity para evitar colisión con android.app.Activity.
 */
public class TourActivity {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("category")
    private String category; // "tour" | "free_tour" | "excursion" | "experience"

    @SerializedName("destinationId")
    private String destinationId;

    @SerializedName("guideId")
    private String guideId;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getCapacity() { return capacity; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getDestinationId() { return destinationId; }
    public String getGuideId() { return guideId; }
}
