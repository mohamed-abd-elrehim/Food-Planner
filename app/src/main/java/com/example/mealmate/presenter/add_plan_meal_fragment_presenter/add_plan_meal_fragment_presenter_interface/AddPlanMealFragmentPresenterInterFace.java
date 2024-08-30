package com.example.mealmate.presenter.add_plan_meal_fragment_presenter.add_plan_meal_fragment_presenter_interface;

import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public interface AddPlanMealFragmentPresenterInterFace {

    void addMealToPaln(MealPlan mealPlan, CustomMeal meal);
    void loadAllMealDetailsById(String id);
    void updateWeekRange(Calendar currentWeek, SimpleDateFormat dateFormat);


}
