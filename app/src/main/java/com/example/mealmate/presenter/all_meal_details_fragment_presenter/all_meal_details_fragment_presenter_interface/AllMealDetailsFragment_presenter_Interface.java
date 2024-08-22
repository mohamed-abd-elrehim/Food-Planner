package com.example.mealmate.presenter.all_meal_details_fragment_presenter.all_meal_details_fragment_presenter_interface;

import com.example.mealmate.model.Step;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;

import java.util.List;

public interface AllMealDetailsFragment_presenter_Interface {
    void loadAllMealDetailsById(String id);
    List<Step> processInstructions(String instructions);
    List<MealMeasureIngredient> getMealMeasureIngredients(CustomMeal meal);

    void addMealToFAV(CustomMeal meal);

     void getFavMeals(String id);

    //void addMealToPlan(CustomMeal customMeal);
    //void loadAllMealDetailsByName(String name);

}
