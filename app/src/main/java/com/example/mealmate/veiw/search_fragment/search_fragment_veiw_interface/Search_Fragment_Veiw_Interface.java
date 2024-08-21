package com.example.mealmate;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;

import java.util.List;

public interface Search_Fragment_Veiw_Interface<T> {
    void showData(List<T> data);
    void showError(String errorMessage);
}
