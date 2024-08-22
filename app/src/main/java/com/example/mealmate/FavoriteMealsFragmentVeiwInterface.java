package com.example.mealmate;

import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;

import java.util.List;

public interface FavoriteMealsFragmentVeiwInterface {
    void showData(List<MealDTO> data);
    void showError();
}
