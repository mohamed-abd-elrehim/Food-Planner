package com.example.mealmate.model.network.network_Interface;

import com.example.mealmate.model.network.MealResponse;

import retrofit2.Response;

public interface NetworkCallback {
    public void onSuccessResult(Response<MealResponse> response);
    public void onFailureResult(String errorMsg);
}
