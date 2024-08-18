package com.example.mealmate.model.database.local_data_source.local_data_source_interface;


import androidx.lifecycle.LiveData;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealWithDetails;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlanWithMeals;

import java.util.List;
public interface MealDataSource {
    LiveData<List<MealDTO>> getAllMeals();
    LiveData<MealWithDetails> getMealWithIngredients(String mealId);
    LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId);
    void insertMeal(MealDTO meal);
    void deleteMeal(MealDTO meal);
}
