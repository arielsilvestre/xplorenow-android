package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class OtpRequest {

    @SerializedName("email")
    private final String email;

    @SerializedName("code")
    private final String code;

    @SerializedName("type")
    private final String type;

    public OtpRequest(String email, String code, String type) {
        this.email = email;
        this.code = code;
        this.type = type;
    }

    /** Constructor para resend-otp (sin campo code) */
    public OtpRequest(String email, String type) {
        this.email = email;
        this.code = null;
        this.type = type;
    }

    public String getEmail() { return email; }
    public String getCode()  { return code; }
    public String getType()  { return type; }
}
