package com.example.mealmate.presenter.plan_of_the_week_fragment_presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.mealmate.presenter.plan_of_the_week_fragment_presenter.plan_of_the_week_fragment_presenter_interface.PlanOfWeekFragmentPresenterInterface;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.veiw.plan_of_the_week_fragment.plan_of_the_week_fragment_interface.PlanOfWeekFragmentVeiwInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PlanOfTheWeekFragmentPresenter implements PlanOfWeekFragmentPresenterInterface {
    private PlanOfWeekFragmentVeiwInterface view;
    private static final String TAG = "PlanOfTheWeekFragmentPresenter";
    private boolean isDataProcessed = false;

    private MealRepository mealRepository;

    private LiveData<List<MealDTO>> allMeals;
    private LiveData<List<MealPlan>> allMealPlans;
    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredient;
    List<MealPlan> allplanMeals = new ArrayList<>();
    List<String> weekRanges = new ArrayList<>();

    public PlanOfTheWeekFragmentPresenter( MealRepository mealRepository, PlanOfWeekFragmentVeiwInterface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }



/*
    @Override
    public void getAllPlanOfWeeksMeals(String email) {
        Log.i(TAG, "Fetching meal plans for email: " + email);
        allMealPlans = mealRepository.getMealPlans(email);
        allMealPlans.observeForever(new Observer<List<MealPlan>>() {
            @Override
            public void onChanged(List<MealPlan> planMeals) {
                if (planMeals != null && !planMeals.isEmpty()) {
                    Log.i(TAG, "Fetched planMeals: " + planMeals.size());
                    allplanMeals.clear();
                    allplanMeals.addAll(planMeals);

                    List<MealDTO> allMeals = new ArrayList<>();
                    AtomicInteger processedCount = new AtomicInteger(0);

                    for (MealPlan mealPlan : planMeals) {
                        mealDTO = mealRepository.getMealById(mealPlan.getMealId());
                        mealDTO.observeForever(new Observer<MealDTO>() {
                            @Override
                            public void onChanged(MealDTO meal) {
                                if (meal != null) {
                                    allMeals.add(meal);
                                    Log.i(TAG, "Meal added: " + meal.getIdMeal());
                                } else {
                                    Log.w(TAG, "Meal data is null for ID: " + mealPlan.getMealId());
                                }

                                // Check if all meals are processed
                                if (processedCount.incrementAndGet() == planMeals.size()) {
                                    if (!allMeals.isEmpty()) {
                                        Log.i(TAG, "All plan of weeks meals processed: " + allMeals.size());
                                        view.showData(allMeals, allplanMeals);
                                        Log.i(TAG, "First meal ID: " + allMeals.get(0).getIdMeal());
                                    }
                                }
                            }
                        });
                    }
                } else {
                    Log.i(TAG, "No meal plans found or planMeals is null");
                    view.showError();
                }

            }
        });
    }
*/

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

    @Override
    public void updateWeekRange(int weekIndex,List<String> weekRanges, SimpleDateFormat dateFormat) {
        // Get the week range from the predefined list
        String weekRange = weekRanges.get(weekIndex);
        view.updateWeekRangeText(weekRange);
        // Extract the start and end dates from the week range string
        String[] dates = weekRange.split(" - ");
        if (dates.length == 2) {
            try {
                // Parse the start and end dates
                Date startDate = dateFormat.parse(dates[0]);
                Date endDate = dateFormat.parse(dates[1]);
                if (startDate != null && endDate != null) {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(startDate);

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(endDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getWeekRange(List<MealPlan> mealPlans) {
        weekRanges.clear();
        if (mealPlans != null) {
            weekRanges.add("Select Week");
            for (MealPlan mealPlan : mealPlans) {
                if (!weekRanges.contains(mealPlan.getDate())) {
                    weekRanges.add(mealPlan.getDate());
                }
            }
            Log.i(TAG, "getWeekRange: "+weekRanges.toString());
            view.setWeekRange(weekRanges);
        }
    }


    @Override
    public void getDays(List<MealPlan> mealPlans) {
        List<String> lsitAvailableDays = new ArrayList<>();
        for (MealPlan mealPlan : mealPlans) {
            if (!lsitAvailableDays.contains(mealPlan.getDayOfWeek())) {
                lsitAvailableDays.add(mealPlan.getDayOfWeek());
            }

            view.setDay(lsitAvailableDays);

        }
    }
}

