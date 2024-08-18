package com.example.mealmate;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.network.MealResponse;

import java.util.List;

public interface HomeFragmentView {

    void showMeals(List<MealDTO> meals);

    void showError(String message);
}
