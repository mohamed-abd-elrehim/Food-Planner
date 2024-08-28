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
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlanWithMeals;

import java.util.List;

@Dao
public interface MealPlanDAO {

    @Transaction
    default void insertMealPlan(MealPlan mealPlan, MealDTO meal, List<MealMeasureIngredient> ingredients) {
        // Insert the favorite meal first
        insertMealPlan(mealPlan);        // Insert the meal first
        insertMeal(meal);
        // Insert all ingredients associated with the meal
        insertIngredients(ingredients);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMealPlan(MealPlan mealPlan);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeal(MealDTO meal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredients(List<MealMeasureIngredient> ingredients);


    // Query to fetch the Plan meal for a specific client
    @Query("SELECT * FROM MealPlan WHERE client_email = :clientEmail")
    LiveData<List<MealPlan>> getMealPlans(String clientEmail);

    // Query to fetch the meal by meal ID
    @Query("SELECT * FROM MealDTO WHERE meal_id = :mealId")
    LiveData<MealDTO> getMealById(String mealId);

    // Query to fetch all ingredients associated with a specific meal ID
    @Query("SELECT * FROM MealMeasureIngredient WHERE meal_id = :mealId")
    LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId);

    // Delete a specific favorite meal
    @Delete
    void deletePlanMeal(MealPlan mealPlan);

    // Delete a meal by its ID
    @Query("DELETE FROM MealDTO WHERE meal_id = :mealId AND meal_type = 'mealplan'")
    void deleteMealById(String mealId);

    // Delete all ingredients associated with a specific meal ID
    @Query("DELETE FROM MealMeasureIngredient WHERE meal_id = :mealId AND meal_type = 'mealplan'")
    void deleteIngredientsByMealId(String mealId);

    @Transaction
    default void deleteMealPlan(MealPlan mealPlan) {
        String mealId = mealPlan.getMealId();
        deletePlanMeal(mealPlan);
        deleteMealById(mealId);
        deleteIngredientsByMealId(mealId);
    }

    // Insert a list of FavoriteMeal entities
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllPlanMeals(List<MealPlan> mealPlans);


    // This query retrieves all favorite meals for a specific user based on their email address.
    // The method is synchronous, meaning it runs on the current thread and returns a List<FavoriteMeal>.
    @Query("SELECT * FROM mealplan WHERE client_email = :email")
    List<MealPlan> getPlanMealsForUserSync(String email);



}
