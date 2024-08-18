package com.example.mealmate;


import android.util.Log;

import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.network.MealResponse;
import com.example.mealmate.model.network.network_Interface.NetworkCallback;
import com.example.mealmate.model.network.network_Interface.RemoteDataSourceImpl;

import java.util.List;

import retrofit2.Response;

public class HomeFragmentPresenterImpl implements HomeFragmentPresenterInterface, NetworkCallback {
    private HomeFragmentView view;
    public static final String TAG = "HomeFragmentPresenterImpl";
    MealRepository mealRepository;
    AppDataBase appDataBase;

    public HomeFragmentPresenterImpl(AppDataBase appDataBase, MealRepository mealRepository, HomeFragmentView view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.appDataBase = appDataBase;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }

    @Override
    public void loadMeals() {
        mealRepository.makeNetworkCallback(this, "randomMeal");
    }

    @Override
    public void onSuccessResult(Response<MealResponse> response) {
        if (response.body() != null && response.body().getMeals() != null) {
            List<MealDTO> meals = response.body().getMeals();
            view.showMeals(meals);
            Log.i(TAG, "Meals loaded successfully: " + meals.size() + " meals found.");
        } else {
            view.showError("No meals found.");
            Log.w(TAG, "Meals response was successful but no meals were found.");
        }
    }

    @Override
    public void onFailureResult(String errorMsg) {
        view.showError("Failed to load meals: " + errorMsg);
        Log.e(TAG, "Error loading meals: " + errorMsg);
    }
}