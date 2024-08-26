package com.shopi.android.receiptreader;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("text")
    private String extractedText;

    public String getExtractedText() {
        return extractedText;
    }
}
