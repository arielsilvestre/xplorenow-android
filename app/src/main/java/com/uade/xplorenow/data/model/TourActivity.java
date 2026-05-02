package com.uade.xplorenow.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("availableSpots")
    private int availableSpots;

    @SerializedName("duration")
    private String duration;

    @SerializedName("meetingPoint")
    private String meetingPoint;

    @SerializedName("whatsIncluded")
    private String whatsIncluded;

    @SerializedName("cancellationPolicy")
    private String cancellationPolicy;

    @SerializedName("departureLat")
    private Double departureLat;

    @SerializedName("departureLng")
    private Double departureLng;

    @SerializedName("language")
    private String language;

    @SerializedName("photos")
    private List<String> photos;

    @SerializedName("guide")
    private TourGuide guide;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getCapacity() { return capacity; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getDestinationId() { return destinationId; }
    public String getGuideId() { return guideId; }
    public int getAvailableSpots() { return availableSpots; }
    public String getDuration() { return duration; }
    public String getMeetingPoint() { return meetingPoint; }
    public String getWhatsIncluded() { return whatsIncluded; }
    public String getCancellationPolicy() { return cancellationPolicy; }
    public Double getDepartureLat() { return departureLat; }
    public Double getDepartureLng() { return departureLng; }
    public String getLanguage() { return language; }
    public List<String> getPhotos() { return photos; }
    public TourGuide getGuide() { return guide; }
}
