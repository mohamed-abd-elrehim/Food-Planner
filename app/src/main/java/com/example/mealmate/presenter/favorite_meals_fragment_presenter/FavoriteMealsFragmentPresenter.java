package com.example.mealmate.presenter.favorite_meals_fragment_presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.mealmate.presenter.favorite_meals_fragment_presenter.favorite_meals_fragment_presenter_interface.FavoriteMealsFragmentPresenterInterface;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.veiw.favorite_meals_fragment.favorite_meals_fragment_veiw_interface.FavoriteMealsFragmentVeiwInterface;

import java.util.ArrayList;
import java.util.List;

public class FavoriteMealsFragmentPresenter implements FavoriteMealsFragmentPresenterInterface {
    private FavoriteMealsFragmentVeiwInterface view;
    private static final String TAG = "FavoriteMealsFragment_presenter";
    private MealRepository mealRepository;
    private AppDataBase appDataBase;

    private LiveData<List<MealDTO>> allMeals;
    private LiveData<List<FavoriteMeal>> favoriteMeal;
    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredient;


    public FavoriteMealsFragmentPresenter(AppDataBase appDataBase, MealRepository mealRepository, FavoriteMealsFragmentVeiwInterface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.appDataBase = appDataBase;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }


    @Override
    public void getAllFAVMeals(String email) {
        favoriteMeal = mealRepository.getFavoriteMeal(email);
        favoriteMeal.observeForever(favoriteMeals -> {
            if (favoriteMeals != null && !favoriteMeals.isEmpty()) {
                List<MealDTO> allMeals = new ArrayList<>(); // Create a mutable list to accumulate meals

                for (FavoriteMeal favoriteMeal : favoriteMeals) {
                    mealDTO = mealRepository.getMealById(favoriteMeal.getMealId());
                    mealDTO.observeForever(meal -> {
                        if (meal != null) {
                            allMeals.add(meal); // Add each meal to the list

                            // Check if all meals have been added
                            if (allMeals.size() == favoriteMeals.size()) {
                                Log.i(TAG, "getAllFAVMeals: "+allMeals.size());
                                view.showData(allMeals);
                                Log.i(TAG, "getAllFAVMeals: "+allMeals.get(0).getIdMeal());
                            }
                        }
                    });
                }
            } else {
                view.showError();
            }
        });
    }


    @Override
    public void deleteFavoriteMeal(FavoriteMeal favoriteMeal) {

        mealRepository.deleteFavoriteMeal( favoriteMeal);
        Log.i(TAG, "deleteProductFromFAV: ");

    }

    @Override
    public void seeMore(CustomMeal customMeal) {

    }


}


