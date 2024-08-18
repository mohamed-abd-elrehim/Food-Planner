package com.example.mealmate.model.network.network_Interface;

import com.example.mealmate.model.network.MealResponse;

import retrofit2.Response;

public interface NetworkCallback<T> {
    void onSuccessResult(Response<T> response);
    void onFailureResult(String errorMsg);
}
