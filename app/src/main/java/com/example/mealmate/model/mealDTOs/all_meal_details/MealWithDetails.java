package com.example.mealmate.model.mealDTOs.all_meal_details;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class MealWithDetails {
    @Embedded
    public MealDTO meal;

    @Relation(
            parentColumn = "meal_id",
            entityColumn = "meal_id"
    )
    public List<MealMeasureIngredient> ingredients;
}
