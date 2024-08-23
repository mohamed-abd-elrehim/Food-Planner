package com.example.mealmate;

import com.example.mealmate.model.MealArea;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;

public interface PlanOfWeekFragmentPresenterInterface {
    void getAllPlanOfWeeksMeals(String email);
    void deletePlanMeal(MealPlan mealPlan);
    void seeMore(CustomMeal customMeal);

}
