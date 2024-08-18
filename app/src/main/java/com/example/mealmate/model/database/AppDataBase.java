package com.example.mealmate.model.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mealmate.model.database.DAOs.FavoriteMealDAO;
import com.example.mealmate.model.database.DAOs.MealDAO;
import com.example.mealmate.model.database.DAOs.MealPlanDAO;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;// Import your FavoriteMealDAO

@Database(entities = {MealDTO.class, MealMeasureIngredient.class, FavoriteMeal.class, MealPlan.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {

    private static AppDataBase appDataBase = null;

    // Abstract methods for DAO access
    public abstract MealDAO getMealDAO();
    public abstract FavoriteMealDAO getFavoriteMealDAO();
    public abstract MealPlanDAO getMealPlanDAO();

    public static synchronized AppDataBase getInstance(Context context) {
        if (appDataBase == null) {
            try {
                appDataBase = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "app_database")
                        .fallbackToDestructiveMigration()  // Optional: Handle migrations by destroying and recreating the database
                        .build();
            } catch (Exception e) {
                Log.e("AppDataBase", "Error creating database", e);
            }
        }
        return appDataBase;
    }
}
