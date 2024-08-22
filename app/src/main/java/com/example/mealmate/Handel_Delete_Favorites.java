package com.example.mealmate;

import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;

public interface Handel_Delete_Favorites {
    void onDeleteFavoritesClick(FavoriteMeal favoriteMeal);

}
