package com.example.mealmate.model.mealDTOs.favorite_meals;



import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;

import java.util.List;

public class FavoriteMealWithMeals {
    @Embedded
    public FavoriteMeal favoriteMeal;

    @Relation(
            parentColumn = "meal_id",
            entityColumn = "meal_id"
    )
    public List<MealDTO> meals;
}
