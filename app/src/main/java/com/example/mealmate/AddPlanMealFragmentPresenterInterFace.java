package com.example.mealmate;

import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;

import java.util.List;

public interface AddPlanMealFragmentPresenterInterFace {

    void addMealToPaln(MealPlan mealPlan, CustomMeal meal);
    void loadAllMealDetailsById(String id);
}
