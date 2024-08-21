package com.example.mealmate.model.database.local_data_source.local_data_source_interface;

import androidx.lifecycle.LiveData;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMealWithMeals;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlanWithMeals;

import java.util.List;
public interface FavoriteMealDataSource {
//    LiveData<List<FavoriteMealWithMeals>> getFavoritesWithMeals(String clientEmail);
//    LiveData<FavoriteMeal> getFavoriteMeal(String mealId, String clientEmail);
//    void insertFavoriteMeal(FavoriteMeal favoriteMeal);
//    void deleteFavoriteMeal(FavoriteMeal favoriteMeal);

    void insertFavoriteMealWithMeals(FavoriteMeal meal, MealDTO mealDTO,List<MealMeasureIngredient> mealDTOS);


}
