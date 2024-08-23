package com.example.mealmate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.veiw.favorite_meals_fragment.favorite_meals_fragment_veiw_interface.FavoriteMealsFragmentVeiwInterface;

import java.util.ArrayList;
import java.util.List;

public class PlanOfTheWeekFragmentPresenter implements PlanOfWeekFragmentPresenterInterface {
    private PlanOfWeekFragmentVeiwInterface view;
    private static final String TAG = "PlanOfTheWeekFragmentPresenter";

    private MealRepository mealRepository;
    private AppDataBase appDataBase;

    private LiveData<List<MealDTO>> allMeals;
    private LiveData<List<MealPlan>> allMealPlans;
    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredient;


    public PlanOfTheWeekFragmentPresenter(AppDataBase appDataBase, MealRepository mealRepository, PlanOfWeekFragmentVeiwInterface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.appDataBase = appDataBase;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }

    @Override
    public void getAllPlanOfWeeksMeals(String email) {
        allMealPlans = mealRepository.getMealPlans(email);
        allMealPlans.observeForever(planMeals -> {
            if (planMeals != null && !planMeals.isEmpty()) {
                List<MealDTO> allMeals = new ArrayList<>(); // Create a mutable list to accumulate meals

                for (MealPlan mealPlan : planMeals) {
                    mealDTO = mealRepository.getMealById(mealPlan.getMealId());
                    mealDTO.observeForever(meal -> {
                        if (meal != null) {
                            allMeals.add(meal); // Add each meal to the list

                            // Check if all meals have been added
                            if (allMeals.size() == planMeals.size()) {
                                Log.i(TAG, "getAllPlanOfWeeksMeals: "+allMeals.size());
                                view.showData(allMeals);
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
    public void deletePlanMeal(MealPlan mealPlan) {
        mealRepository.deleteMealPlan( mealPlan);
        Log.i(TAG, "deletePlanMeal: ");
    }

    @Override
    public void seeMore(CustomMeal customMeal) {

    }
}