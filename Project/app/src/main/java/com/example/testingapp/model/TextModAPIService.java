package com.example.testingapp.model;

// import retrofit, needed for API calls
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface TextModAPIService {
    @POST("v1/moderations")
    Call<TextModResponse> checkText(
            @Header("Authorization") String authToken,
            @Body TextModRequest request
    );
}