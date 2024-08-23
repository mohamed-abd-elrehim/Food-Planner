package com.example.mealmate;

import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;

import java.util.List;

public interface PlanOfWeekFragmentVeiwInterface {
    void showData(List<MealDTO> data);
    void showError();
}
