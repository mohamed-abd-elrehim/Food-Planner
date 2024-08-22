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
import com.example.mealmate.model.mealDTOs.all_meal_details.MealWithDetails;

import java.util.List;

@Dao
public interface MealDAO {

//    // Insert a meal along with its associated ingredients in a single transaction
//
//    @Transaction
//    default void insertMealWithDetails(MealDTO meal, List<MealMeasureIngredient> ingredients) {
//        // Insert the meal first
//        insertMeal(meal);
//        // Insert all ingredients associated with the meal
//        insertIngredients(ingredients);
//    }
//
//    //   Insert a MealDTO into the database. If the meal already exists, it will be replaced.
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertMeal(MealDTO meal);
//
//    // Insert a MealMeasureIngredient into the database. If the ingredient already exists, it will be replaced.
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertIngredient(MealMeasureIngredient ingredient);
//
//    // Insert a list of MealMeasureIngredients into the database. If any ingredient already exists, it will be replaced.
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertIngredients(List<MealMeasureIngredient> ingredients);


//    // Delete a meal and its associated ingredients in a single transaction
//    @Transaction
//    default void deleteMealWithIngredients(MealDTO meal) {
//        // Delete ingredients associated with the meal
//        deleteIngredientsByMealId(meal.getIdMeal());
//        // Delete the meal itself
//        deleteMeal(meal);
//    }
//
//    // Query to delete all ingredients associated with a specific meal_id
//    @Query("DELETE FROM MealMeasureIngredient WHERE meal_id = :mealId")
//    void deleteIngredientsByMealId(String mealId);
//
//    // Delete a meal from the database
//    @Delete
//    void deleteMeal(MealDTO meal);
//
//    // Query to fetch a meal along with its associated ingredients by meal_id
//    @Transaction
//    @Query("SELECT * FROM MealDTO WHERE meal_id = :mealId")
//    LiveData<MealWithDetails> getMealWithIngredients(String mealId);
//
//    // Query to fetch all meals from the database
//    @Query("SELECT * FROM MealDTO")
//    LiveData<List<MealDTO>> getAllMeals();
//
//    // Query to fetch all ingredients associated with a specific meal_id
//    @Query("SELECT * FROM MealMeasureIngredient WHERE meal_id = :mealId")
//    LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId);
//
//
}
