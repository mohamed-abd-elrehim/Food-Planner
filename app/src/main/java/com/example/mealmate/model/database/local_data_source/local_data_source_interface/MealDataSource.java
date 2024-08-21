package com.example.mealmate.model.database.local_data_source.local_data_source_interface;

import androidx.lifecycle.LiveData;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealWithDetails;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlanWithMeals;

import java.util.List;

public interface MealDataSource {

//    // Retrieve all meals from the database
//    LiveData<List<MealDTO>> getAllMeals();
//
//    // Retrieve a meal with its associated ingredients by meal_id
//    LiveData<MealWithDetails> getMealWithIngredients(String mealId);
//
//    // Retrieve all ingredients associated with a specific meal_id
//    LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId);
//
//    // Insert a new meal into the database
//    void insertMeal(MealDTO meal);
//
//    // Delete a meal from the database
//    void deleteMeal(MealDTO meal);
//
//
//    // Insert a list of meal measures and ingredients associated with a meal
//    void insertIngredients(List<MealMeasureIngredient> ingredients);
//
//    // Delete ingredients associated with a specific meal_id
//    void deleteIngredientsByMealId(String mealId);

    void insertMealWithDetails(MealDTO meal, List<MealMeasureIngredient> ingredients);

}
