package com.example.mealmate;

import com.example.mealmate.model.Step;
import com.example.mealmate.model.database.DAOs.FavoriteMealDAO;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealWithDetails;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMealWithMeals;

import java.util.ArrayList;
import java.util.List;

public interface AllMealDetailsFragment_presenter_Interface {
    void loadAllMealDetailsById(String id);
    List<Step> processInstructions(String instructions);
    List<MealMeasureIngredient> getMealMeasureIngredients(CustomMeal meal);

    void addMealToFAV(CustomMeal meal);


    //void addMealToPlan(CustomMeal customMeal);
    //void loadAllMealDetailsByName(String name);

}
