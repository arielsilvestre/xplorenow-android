package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ToggleDestinationRequest {

    @SerializedName("destinationId")
    private final String destinationId;

    public ToggleDestinationRequest(String destinationId) {
        this.destinationId = destinationId;
    }
}
