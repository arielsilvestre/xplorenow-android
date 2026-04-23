package com.uade.xplorenow.data.model;

import com.google.gson.annotations.SerializedName;

public class Reservation {

    @SerializedName("id")
    private String id;

    @SerializedName("date")
    private String date; // formato YYYY-MM-DD

    @SerializedName("people")
    private int people;

    @SerializedName("status")
    private String status; // "pending" | "confirmed" | "cancelled"

    @SerializedName("activityId")
    private String activityId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("activity")
    private TourActivity activity; // objeto anidado

    /** Constructor para reconstruir una Reservation desde la caché local (Room). */
    public Reservation(String id, String activityId, String date, int people, String status) {
        this.id = id;
        this.activityId = activityId;
        this.date = date;
        this.people = people;
        this.status = status;
    }

    public String getId() { return id; }
    public String getDate() { return date; }
    public int getPeople() { return people; }
    public String getStatus() { return status; }
    public String getActivityId() { return activityId; }
    public String getUserId() { return userId; }
    public TourActivity getActivity() { return activity; }
}
