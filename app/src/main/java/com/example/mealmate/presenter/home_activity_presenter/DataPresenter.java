package com.example.mealmate.presenter.home_activity_presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.presenter.home_activity_presenter.home_activity_presenter_interface.DataRepository;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class DataPresenter implements DataRepository {
    private static final String TAG = "DataPresenter";

    private MealRepository mealRepository;
    private AppDataBase appDataBase;

    private LiveData<List<MealDTO>> allMeals;
    private LiveData<List<FavoriteMeal>> favoriteMealsLiveData;
    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredientLiveData;
    private LiveData<List<MealPlan>> mealPlansLiveData;

    private List<MealPlan> listMealPlans= new ArrayList<>();
    private List<FavoriteMeal> listFavoriteMeal= new ArrayList<>();
    String currentUserEmail;

    private FirebaseFirestore db;

    public DataPresenter(AppDataBase appDataBase, MealRepository mealRepository) {
        this.mealRepository = mealRepository;
        this.appDataBase = appDataBase;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
        this.db = FirebaseFirestore.getInstance();



    }



    @Override
    public void backupData() {
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (currentUserEmail == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        favoriteMealsLiveData = mealRepository.getFavoriteMeal(currentUserEmail);
        mealPlansLiveData = mealRepository.getMealPlans(currentUserEmail);

        favoriteMealsLiveData.observeForever(favoriteMeals -> {
            if (favoriteMeals != null && !favoriteMeals.isEmpty()) {
                listFavoriteMeal.addAll(favoriteMeals);
            } else {
                Log.e(TAG, "No favorite meals found to back up.");
            }
        });

        mealPlansLiveData.observeForever(mealPlans -> {
            if (mealPlans != null && !mealPlans.isEmpty()) {
                listMealPlans.addAll(mealPlans);
            } else {
                Log.e(TAG, "No meal plans found to back up.");
            }
        });

        // Wait for data fetching to complete before performing backup
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!listFavoriteMeal.isEmpty() || !listMealPlans.isEmpty()) {
                performBackupProcess(listFavoriteMeal, listMealPlans);
            } else {
                Log.e(TAG, "No data to backup.");
            }
        }, 5000); // Adjust delay as necessary
    }

    private void performBackupProcess(List<FavoriteMeal> favoriteMeals, List<MealPlan> mealPlans) {
        List<MealDTO> allMeals = new ArrayList<>();
        List<MealMeasureIngredient> allMealMeasureIngredients = new ArrayList<>();
        AtomicInteger remainingMeals = new AtomicInteger(
                (favoriteMeals != null ? favoriteMeals.size() : 0) +
                        (mealPlans != null ? mealPlans.size() : 0)
        );

        if (favoriteMeals != null) {
            for (FavoriteMeal favoriteMeal : favoriteMeals) {
                processMeal(favoriteMeal.getMealId(), allMeals, allMealMeasureIngredients, remainingMeals,
                        () -> performBackup(favoriteMeals, null, allMeals, allMealMeasureIngredients));
            }
        }

        if (mealPlans != null) {
            for (MealPlan mealPlan : mealPlans) {
                processMeal(mealPlan.getMealId(), allMeals, allMealMeasureIngredients, remainingMeals,
                        () -> performBackup(null, mealPlans, allMeals, allMealMeasureIngredients));
            }
        }

        if (remainingMeals.get() == 0) {
            performBackup(
                    favoriteMeals != null ? favoriteMeals : null,
                    mealPlans != null ? mealPlans : null,
                    allMeals.isEmpty() ? null : allMeals,
                    allMealMeasureIngredients.isEmpty() ? null : allMealMeasureIngredients
            );
        }
    }

    private void processMeal(String mealId, List<MealDTO> allMeals, List<MealMeasureIngredient> allMealMeasureIngredients,
                             AtomicInteger remainingMeals, Runnable onComplete) {
        mealDTO = mealRepository.getMealById(mealId);
        mealDTO.observeForever(meal -> {
            if (meal != null) {
                allMeals.add(meal);
                mealMeasureIngredientLiveData = mealRepository.getIngredientsByMealId(meal.getIdMeal());
                mealMeasureIngredientLiveData.observeForever(mealMeasureIngredients -> {
                    if (mealMeasureIngredients != null && !mealMeasureIngredients.isEmpty()) {
                        allMealMeasureIngredients.addAll(mealMeasureIngredients);
                    }
                    if (remainingMeals.decrementAndGet() == 0) {
                        onComplete.run();
                    }
                });
            } else {
                if (remainingMeals.decrementAndGet() == 0) {
                    onComplete.run();
                }
            }
        });
    }

    private void performBackup(List<FavoriteMeal> favoriteMeals, List<MealPlan> mealPlans,
                               List<MealDTO> meals, List<MealMeasureIngredient> mealMeasureIngredients) {
        try {
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            if (currentUserEmail == null) {
                Log.e(TAG, "User not authenticated");
                return;
            }

            Gson gson = new Gson();

            // Parent collection reference
            CollectionReference parentCollection = db.collection("backupDataCollection").document(currentUserEmail).collection("backup");

            if (favoriteMeals != null) {
                Map<String, Object> favoriteMealsData = new HashMap<>();
                favoriteMealsData.put("favoriteMeals", gson.toJson(favoriteMeals));

                parentCollection.document("favoriteMeals")
                        .set(favoriteMealsData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Favorite meals backup successful"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error backing up favorite meals", e));
            }

            if (mealPlans != null) {
                Map<String, Object> mealPlansData = new HashMap<>();
                mealPlansData.put("mealPlans", gson.toJson(mealPlans));

                parentCollection.document("mealPlans")
                        .set(mealPlansData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Meal plans backup successful"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error backing up meal plans", e));
            }

            if (meals != null) {
                Map<String, Object> mealsData = new HashMap<>();
                mealsData.put("meals", gson.toJson(meals));

                parentCollection.document("meals")
                        .set(mealsData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Meals backup successful"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error backing up meals", e));
            }

            if (mealMeasureIngredients != null) {
                Map<String, Object> ingredientsData = new HashMap<>();
                ingredientsData.put("ingredients", gson.toJson(mealMeasureIngredients));

                parentCollection.document("ingredients")
                        .set(ingredientsData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Ingredients backup successful"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error backing up ingredients", e));
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to backup data", e);
        }
    }

    public void restoreData() {
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (currentUserEmail == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        // Fetch backup data from Firestore subcollection
        db.collection("backupDataCollection")
                .document(currentUserEmail)
                .collection("backup")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Log.e(TAG, "No backup data found");
                        return;
                    }

                    // Iterate over documents in the snapshot
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        try {
                            Gson gson = new Gson();

                            // Retrieve JSON strings from Firestore
                            String favoriteMealsJson = document.getString("favoriteMeals");
                            String mealsJson = document.getString("meals");
                            String ingredientsJson = document.getString("ingredients");
                            String mealPlansJson = document.getString("mealPlans");

                            // Define TypeTokens for parsing
                            Type favoriteMealsType = new TypeToken<List<FavoriteMeal>>() {}.getType();
                            Type mealsType = new TypeToken<List<MealDTO>>() {}.getType();
                            Type ingredientsType = new TypeToken<List<MealMeasureIngredient>>() {}.getType();
                            Type mealPlansType = new TypeToken<List<MealPlan>>() {}.getType();

                            // Parse JSON data
                            List<FavoriteMeal> favoriteMeals = gson.fromJson(favoriteMealsJson, favoriteMealsType);
                            List<MealDTO> meals = gson.fromJson(mealsJson, mealsType);
                            List<MealMeasureIngredient> ingredients = gson.fromJson(ingredientsJson, ingredientsType);
                            List<MealPlan> mealPlans = gson.fromJson(mealPlansJson, mealPlansType);

                            // Insert data into the repository
                            if (favoriteMeals != null) {
                                mealRepository.insertAllFavoriteMeals(favoriteMeals);
                            }
                            if (meals != null) {
                                mealRepository.insertAllMeals(meals);
                            }
                            if (ingredients != null) {
                                mealRepository.insertAllIngredients(ingredients);
                            }
                            if (mealPlans != null) {
                                mealRepository.insertAllPlanMeals(mealPlans);
                            }

                            Log.d(TAG, "Data restored successfully");

                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "Error parsing JSON data", e);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching backup data", e));
    }


}









