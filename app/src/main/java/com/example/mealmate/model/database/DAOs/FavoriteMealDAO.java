package com.example.mealmate.model.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMealWithMeals;

import java.util.List;

@Dao
public interface FavoriteMealDAO {
    //
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertFavoriteMeal(FavoriteMeal favoriteMeal);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertMeal(MealDTO meal);
//
//    @Delete
//    void deleteFavoriteMeal(FavoriteMeal favoriteMeal);
//    @Delete
//    void deleteMeal(MealDTO meal);

    @Transaction
    default void insertFavoriteMeal(FavoriteMeal favoriteMeal,MealDTO meal, List<MealMeasureIngredient> ingredients) {
       // Insert the favorite meal first
        insertFavoriteMeal(favoriteMeal);
        // Insert the meal first
        insertMeal(meal);
        // Insert all ingredients associated with the meal
        insertIngredients(ingredients);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteMeal(FavoriteMeal meal);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeal(MealDTO meal);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredients(List<MealMeasureIngredient> ingredients);


//    @Transaction
//    @Query("SELECT * FROM FavoriteMeal WHERE client_email = :clientEmail")
//    LiveData<List<FavoriteMealWithMeals>> getFavoritesWithMeals(String clientEmail);
//
//    @Query("SELECT * FROM FavoriteMeal WHERE meal_id = :mealId AND client_email = :clientEmail")
//    LiveData<FavoriteMeal> getFavoriteMeal(String mealId, String clientEmail);
//
//    @Query("SELECT * FROM MealDTO WHERE meal_id = :mealId")
//    LiveData<List<MealDTO>> getMealsByMealId(String mealId);
}
