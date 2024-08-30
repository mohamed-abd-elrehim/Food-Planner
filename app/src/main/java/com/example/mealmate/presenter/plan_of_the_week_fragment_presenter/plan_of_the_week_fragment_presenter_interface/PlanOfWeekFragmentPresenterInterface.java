
package com.example.mealmate.presenter.plan_of_the_week_fragment_presenter.plan_of_the_week_fragment_presenter_interface;

import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public interface PlanOfWeekFragmentPresenterInterface {
    void getAllPlanOfWeeksMeals(String email);
    void deletePlanMeal(MealPlan mealPlan);
    void seeMore(CustomMeal customMeal);
    void  updateWeekRange(int weekIndex, List<String> weekRanges, SimpleDateFormat dateFormat);
    void getWeekRange(List<MealPlan> mealPlans);
    void getDays(List<MealPlan> mealPlans);
}
