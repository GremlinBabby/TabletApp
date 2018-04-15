package com.example.camil.tabletapp.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("FcmMessage")
    @Expose
    private String fcmMessage;

    public Data(String fcmMessage) {
        this.fcmMessage = fcmMessage;
    }

    public String getPhoneLockStatus() {
        return fcmMessage;
    }

    public void setPhoneLockStatus(String phoneLockStatus) {
        this.fcmMessage = phoneLockStatus;
    }
}
