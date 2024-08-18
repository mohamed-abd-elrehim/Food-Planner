package com.example.mealmate.model.network;

import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealCategoryResponse {
    @SerializedName("categories")
    private List<MealCategory> categories;

    public List<MealCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<MealCategory> categories) {
        this.categories = categories;
    }
}
