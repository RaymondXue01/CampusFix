package com.example.testingapp.model;

import com.google.gson.annotations.SerializedName;
// flags the moderation API, used in TextModReponse.java
public class TextModResult {
    @SerializedName("flagged")
    private boolean flagged;

    public boolean isFlagged() {
        return flagged;
    }
}
