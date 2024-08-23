package com.example.mealmate;
import androidx.lifecycle.LiveData;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import java.util.List;



public interface DataRepository {
    // Existing methods...
    void backupData(String userEmail);
    void restoreData(String userEmail);

    interface DataCallback {
        void onDataLoaded(List<MealDTO> meals, List<MealMeasureIngredient> ingredients, List<FavoriteMeal> favorites);
        void onDataNotAvailable(String errorMessage);
    }
}
