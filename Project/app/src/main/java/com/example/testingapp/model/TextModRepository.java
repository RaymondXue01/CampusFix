package com.example.testingapp.model;

// imports
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// handles the calls to the API
public class TextModRepository {
    private final TextModAPIService apiService;
    // stores key
    private final String apiKey;

    public TextModRepository(String apiKey) {
        this.apiKey = apiKey;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(TextModAPIService.class);
    }

    // method to check the text
    public void checkText(String text, TextModCallback callback) {
        // getting key
        String authHeader = "Bearer " + apiKey;

        apiService.checkText(authHeader, new TextModRequest(text))
                .enqueue(new Callback<TextModResponse>() {
                    @Override
                    public void onResponse(Call<TextModResponse> call,
                                           Response<TextModResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onResult(response.body().isFlagged());
                        } else {
                            callback.onError("Server error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TextModResponse> call, Throwable t) {
                        //error
                        callback.onError(t.getMessage());
                    }
                });
    }

    public interface TextModCallback {
        void onResult(boolean isFlagged);
        void onError(String error);
    }
}
