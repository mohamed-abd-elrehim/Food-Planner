package com.example.mealmate.model.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlanWithMeals;

import java.util.List;

@Dao
public interface MealPlanDAO {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertMealPlan(MealPlan mealPlan);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertMeal(MealDTO meal);
//
//    @Delete
//    void deleteMealPlan(MealPlan mealPlan);
//    @Delete
//    void deleteMeal(MealDTO meal);
//
//    @Transaction
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertPlanWithMeal(MealDTO meal, List<MealDTO> mealDTOS);
//
//
//    @Transaction
//    @Query("SELECT * FROM MealPlan WHERE client_email = :clientEmail")
//    LiveData<List<MealPlanWithMeals>> getMealPlansWithMeals(String clientEmail);
//
//    @Query("SELECT * FROM MealPlan WHERE meal_id = :mealId AND client_email = :clientEmail")
//    LiveData<MealPlan> getMealPlan(String mealId, String clientEmail);
//
//    @Query("SELECT * FROM MealDTO WHERE meal_id = :mealId")
//    LiveData<List<MealDTO>> getMealsByMealId(String mealId);

}
