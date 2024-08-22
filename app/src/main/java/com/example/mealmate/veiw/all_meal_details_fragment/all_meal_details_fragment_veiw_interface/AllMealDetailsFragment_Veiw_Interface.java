package com.example.mealmate.veiw.all_meal_details_fragment.all_meal_details_fragment_veiw_interface;

import com.example.mealmate.model.mealDTOs.CustomMeal;

import java.util.List;

public interface AllMealDetailsFragment_Veiw_Interface {
    void showData(List<CustomMeal> data);
    void showError(String errorMessage);
}
