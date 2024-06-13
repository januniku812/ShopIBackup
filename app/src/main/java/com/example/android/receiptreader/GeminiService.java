package com.example.android.receiptreader;
import android.graphics.Bitmap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GeminiService {
    @POST("image-to-text")
    Call<ApiResponse> extractTextFromImage(@Body Bitmap imageData);
}
