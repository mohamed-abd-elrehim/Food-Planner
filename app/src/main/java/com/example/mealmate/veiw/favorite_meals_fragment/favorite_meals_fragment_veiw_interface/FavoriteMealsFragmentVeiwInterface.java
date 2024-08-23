package com.example.mealmate.veiw.favorite_meals_fragment.favorite_meals_fragment_veiw_interface;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;

import java.util.List;

public interface FavoriteMealsFragmentVeiwInterface {
    void showData(List<MealDTO> data);
    void showError();
}
