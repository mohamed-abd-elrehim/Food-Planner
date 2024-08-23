package com.example.mealmate.presenter.plan_of_the_week_fragment_presenter.plan_of_the_week_fragment_presenter_interface;

import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;

public interface PlanOfWeekFragmentPresenterInterface {
    void getAllPlanOfWeeksMeals(String email);
    void deletePlanMeal(MealPlan mealPlan);
    void seeMore(CustomMeal customMeal);

}
