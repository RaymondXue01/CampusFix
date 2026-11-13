package com.example.testingapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TextModResponse {
    // implementing the request portion which is what app will return once API check is complete
    @SerializedName("results")
    private List<TextModResult> results;

    public boolean isFlagged() {
        if (results != null && !results.isEmpty()) {
            return results.get(0).isFlagged();
        }
        return false;
    }
}
