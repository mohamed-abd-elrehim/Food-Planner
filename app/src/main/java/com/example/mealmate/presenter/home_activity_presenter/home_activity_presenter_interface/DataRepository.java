package com.example.mealmate.presenter.home_activity_presenter.home_activity_presenter_interface;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import java.util.List;



public interface DataRepository {
    // Existing methods...
    void backupData();
    void restoreData();

    interface DataCallback {
        void onDataLoaded(List<MealDTO> meals, List<MealMeasureIngredient> ingredients, List<FavoriteMeal> favorites);
        void onDataNotAvailable(String errorMessage);
    }
}
