package com.example.mealmate.model.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealWithDetails;

import java.util.List;

@Dao
public interface MealDAO {
    @Insert
    void insertMeal(MealDTO meal);

    @Insert
    void insertIngredient(MealMeasureIngredient ingredient);

    @Delete
    void deleteMeal(MealDTO meal);

    @Delete
    void deleteIngredient(MealMeasureIngredient ingredient);

    @Transaction
    @Query("SELECT * FROM MealDTO WHERE meal_id = :mealId")
    LiveData<MealWithDetails> getMealWithIngredients(String mealId);

    @Query("SELECT * FROM MealDTO")
    LiveData<List<MealDTO>> getAllMeals();

    @Query("SELECT * FROM MealMeasureIngredient WHERE meal_id = :mealId")
    LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId);
}
