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
import java.util.concurrent.atomic.AtomicInteger;

public class FavoriteMealsFragmentPresenter implements FavoriteMealsFragmentPresenterInterface {
    private FavoriteMealsFragmentVeiwInterface view;
    private static final String TAG = "FavoriteMealsFragment_presenter";
    private MealRepository mealRepository;

    private LiveData<List<MealDTO>> allMeals;
    private LiveData<List<FavoriteMeal>> favoriteMeal;
    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredient;
    private List<FavoriteMeal> favorites=new ArrayList<>();
    private boolean isDataProcessed = false;


    public FavoriteMealsFragmentPresenter( MealRepository mealRepository, FavoriteMealsFragmentVeiwInterface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }

    @Override
    public void getAllFAVMeals(String email) {
        // Avoid processing if already done
        if (isDataProcessed) return;
        isDataProcessed = true; // Mark data as being processed

        Log.i(TAG, "Fetching favorite meals for email: " + email);

        favoriteMeal = mealRepository.getFavoriteMeal(email);
        favoriteMeal.observeForever(favoriteMeals -> {
            // Ensure we process data only if it hasn't been processed yet
            if (isDataProcessed) {
                if (favoriteMeals != null && !favoriteMeals.isEmpty()) {
                    Log.i(TAG, "Favorite meals found: " + favoriteMeals.size());

                    List<MealDTO> allMeals = new ArrayList<>();
                    List<FavoriteMeal> favorites = new ArrayList<>(favoriteMeals);
                    AtomicInteger processedCount = new AtomicInteger(0);

                    for (FavoriteMeal favoriteMeal : favorites) {
                        observeMeal(favoriteMeal.getMealId(), allMeals, favorites.size(), processedCount);
                    }
                } else {
                    Log.i(TAG, "No favorite meals found or favoriteMeals is null");
                    view.showError();
                    isDataProcessed = false; // Reset flag if no meals found
                }
            } else {
                Log.i(TAG, "Data was already processed");
            }
        });
    }

    private void observeMeal(String mealId, List<MealDTO> allMeals, int totalMeals, AtomicInteger processedCount) {
        Log.i(TAG, "Fetching meal with ID: " + mealId);

        mealDTO = mealRepository.getMealById(mealId);
        mealDTO.observeForever(meal -> {
            if (meal != null) {
                allMeals.add(meal);
                Log.i(TAG, "Meal added: " + meal.getIdMeal());
            }

            // Check if all meals are processed
            if (processedCount.incrementAndGet() == totalMeals) {
                if (!allMeals.isEmpty()) {
                    Log.i(TAG, "All favorite meals processed: " + allMeals.size());
                    view.showData(allMeals);
                    Log.i(TAG, "First meal ID: " + allMeals.get(0).getIdMeal());
                } else {
                    Log.i(TAG, "No meals found after processing favorites");
                    view.showError();
                }
                isDataProcessed = false; // Reset flag after processing
            }
        });
    }

    @Override
    public void deleteFavoriteMeal(FavoriteMeal favoriteMeal) {
        //mealRepository.deleteFavorite( favoriteMeal);
        mealRepository.deleteFavoriteMeal( favoriteMeal);
        Log.i(TAG, "deleteProductFromFAV: ");


    }

    @Override
    public void seeMore(CustomMeal customMeal) {

    }


}


