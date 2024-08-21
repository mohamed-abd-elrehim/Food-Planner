package com.example.mealmate.model.network;

import com.example.mealmate.model.MealArea;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomMealResponse {
    @SerializedName("meals")
    private List<CustomMeal> customMeals;

    public List<CustomMeal> getCustomMeals() {
        return customMeals;
    }

    public void setCustomMeals(List<CustomMeal> customMeals) {
        this.customMeals = customMeals;
    }
}
