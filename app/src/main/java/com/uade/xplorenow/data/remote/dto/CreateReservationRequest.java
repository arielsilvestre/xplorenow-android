package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CreateReservationRequest {

    @SerializedName("activityId")
    private final String activityId;

    @SerializedName("date")
    private final String date; // formato YYYY-MM-DD

    @SerializedName("people")
    private final int people;

    public CreateReservationRequest(String activityId, String date, int people) {
        this.activityId = activityId;
        this.date = date;
        this.people = people;
    }
}
