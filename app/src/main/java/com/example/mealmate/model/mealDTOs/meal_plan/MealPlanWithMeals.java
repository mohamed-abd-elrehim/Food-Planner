package com.example.mealmate.model.mealDTOs.meal_plan;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;

import java.util.List;

public class MealPlanWithMeals {
    @Embedded
    public MealPlan mealPlan;

    @Relation(
            parentColumn = "meal_id",
            entityColumn = "meal_id"
    )
    public List<MealDTO> meals;
}