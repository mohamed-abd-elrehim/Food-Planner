package com.example.mealmate.model.network;

import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealIngredient;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealIngredientResponse {
    @SerializedName("meals")
    private List<MealIngredient> mealIngredients;

    public List<MealIngredient> getIngredients() {
        return mealIngredients;
    }

    public void setIngredients(List<MealIngredient> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }
}
