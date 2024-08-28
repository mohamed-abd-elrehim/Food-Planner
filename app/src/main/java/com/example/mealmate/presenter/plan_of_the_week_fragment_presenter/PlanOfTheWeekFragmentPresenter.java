package com.example.mealmate.presenter.plan_of_the_week_fragment_presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.mealmate.presenter.plan_of_the_week_fragment_presenter.plan_of_the_week_fragment_presenter_interface.PlanOfWeekFragmentPresenterInterface;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.veiw.plan_of_the_week_fragment.plan_of_the_week_fragment_interface.PlanOfWeekFragmentVeiwInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PlanOfTheWeekFragmentPresenter implements PlanOfWeekFragmentPresenterInterface {
    private PlanOfWeekFragmentVeiwInterface view;
    private static final String TAG = "PlanOfTheWeekFragmentPresenter";
    private boolean isDataProcessed = false;

    private MealRepository mealRepository;
    private AppDataBase appDataBase;

    private LiveData<List<MealDTO>> allMeals;
    private LiveData<List<MealPlan>> allMealPlans;
    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredient;
    List<MealPlan> allplanMeals = new ArrayList<>();


    public PlanOfTheWeekFragmentPresenter(AppDataBase appDataBase, MealRepository mealRepository, PlanOfWeekFragmentVeiwInterface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.appDataBase = appDataBase;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }

    @Override
    public void getAllPlanOfWeeksMeals(String email) {
        // Avoid processing if already done
        if (isDataProcessed) return;
        isDataProcessed = true; // Mark data as being processed

        Log.i(TAG, "Fetching meal plans for email: " + email);

        allMealPlans = mealRepository.getMealPlans(email);
        allMealPlans.observeForever(planMeals -> {
            // Ensure we process data only if it hasn't been processed yet
            if (isDataProcessed) {
                if (planMeals != null && !planMeals.isEmpty()) {
                    Log.i(TAG, "Fetched planMeals: " + planMeals.size());
                    allplanMeals.clear();
                    allplanMeals.addAll(planMeals);
                    List<MealDTO> allMeals = new ArrayList<>();
                    AtomicInteger processedCount = new AtomicInteger(0);

                    for (MealPlan mealPlan : planMeals) {
                        observeMeal(mealPlan.getMealId(), allMeals, planMeals.size(), processedCount);
                    }
                } else {
                    Log.i(TAG, "No meal plans found or planMeals is null");
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
            } else {
                Log.w(TAG, "Meal data is null for ID: " + mealId);
            }

            // Check if all meals are processed
            if (processedCount.incrementAndGet() == totalMeals) {
                if (!allMeals.isEmpty()) {
                    Log.i(TAG, "All plan of weeks meals processed: " + allMeals.size());
                    view.showData(allMeals, allplanMeals);
                    Log.i(TAG, "First meal ID: " + allMeals.get(0).getIdMeal());
                } else {
                    Log.i(TAG, "No meals found after processing plans");
                    view.showError();
                }
                isDataProcessed = false; // Reset flag after processing
            }
        });
    }




    @Override
    public void deletePlanMeal(MealPlan mealPlan) {
        //mealRepository.deletePlanMeal(mealPlan);
        mealRepository.deleteMealPlan( mealPlan);
        Log.i(TAG, "deletePlanMeal: ");
    }

    @Override
    public void seeMore(CustomMeal customMeal) {

    }
}