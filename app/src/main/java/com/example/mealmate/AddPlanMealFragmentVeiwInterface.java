package com.example.mealmate;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import java.util.List;

public interface AddPlanMealFragmentVeiwInterface {
    void showData(List<CustomMeal> data);
    void showError(String errorMessage);
}
