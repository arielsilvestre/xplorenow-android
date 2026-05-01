package com.uade.xplorenow.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {

    @SerializedName("email")
    private final String email;

    @SerializedName("code")
    private final String code;

    @SerializedName("newPassword")
    private final String newPassword;

    public ResetPasswordRequest(String email, String code, String newPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getEmail()       { return email; }
    public String getCode()        { return code; }
    public String getNewPassword() { return newPassword; }
}
