package com.example.mealmate;

import androidx.lifecycle.LiveData;

import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;

import java.util.List;

public interface FavoriteMealsFragmentPresenterInterface {
    void getAllFAVMeals(String email);
    void deleteFavoriteMeal(FavoriteMeal favoriteMeal);
    void seeMore(CustomMeal customMeal);

}
