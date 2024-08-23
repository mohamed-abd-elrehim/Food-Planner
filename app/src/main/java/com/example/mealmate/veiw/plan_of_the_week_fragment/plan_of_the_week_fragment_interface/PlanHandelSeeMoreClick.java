package com.example.mealmate.veiw.plan_of_the_week_fragment.plan_of_the_week_fragment_interface;


import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;

public interface PlanHandelSeeMoreClick {
    void onSeeMoreClick(MealDTO meal, MealPlan mealPlan);

}

