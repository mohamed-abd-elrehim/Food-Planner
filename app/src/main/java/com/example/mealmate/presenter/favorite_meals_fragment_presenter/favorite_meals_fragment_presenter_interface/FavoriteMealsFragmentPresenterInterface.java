package com.example.mealmate.presenter.favorite_meals_fragment_presenter.favorite_meals_fragment_presenter_interface;

import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;

public interface FavoriteMealsFragmentPresenterInterface {
    void getAllFAVMeals(String email);
    void deleteFavoriteMeal(FavoriteMeal favoriteMeal);
    void seeMore(CustomMeal customMeal);

}
