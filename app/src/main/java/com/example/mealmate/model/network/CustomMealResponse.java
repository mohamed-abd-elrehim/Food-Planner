package com.example.mealmate.model.network;

import com.example.mealmate.model.MealArea;
import com.example.mealmate.model.MealIngredient;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealAreaResponse {
    @SerializedName("meals")
    private List<MealArea> mealAreas;

    public List<MealArea> getMealAreas() {
        return mealAreas;
    }

    public void setMealAreas(List<MealArea> mealAreas) {
        this.mealAreas = mealAreas;
    }
}
