package com.example.mealmate.model.database.local_data_source.local_data_source_interface;


import androidx.lifecycle.LiveData;
import androidx.room.Query;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlanWithMeals;

import java.util.List;

public interface MealPlanDataSource {
    void insertMealPlan(MealPlan mealPlan, MealDTO meal, List<MealMeasureIngredient> ingredients);
    LiveData<List<MealPlan>> getMealPlans(String clientEmail);
    LiveData<MealDTO> getMealById(String mealId);
    LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId);
    void deleteMealPlan (MealPlan mealPlan);
    void insertAllPlanMeals(List<MealPlan> mealPlans);
    List<MealPlan> getPlanMealsForUserSync(String email);
    void deletePlanMeal(MealPlan mealPlan);


}
