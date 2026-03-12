package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("data")
    private T data;

    @SerializedName("message")
    private String message;

    public T getData() { return data; }
    public String getMessage() { return message; }
}
