package com.example.mealmate;

import android.util.Log;

import androidx.lifecycle.LiveData;

import android.util.Log;

import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private LiveData<List<FavoriteMeal>> favoriteMeal;
    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredient;
    private LiveData<List<MealPlan>> mealPlan;

    private FirebaseFirestore db;

    public DataPresenter(AppDataBase appDataBase, MealRepository mealRepository) {
        this.mealRepository = mealRepository;
        this.appDataBase = appDataBase;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void backupData(String userEmail) {
        favoriteMeal = mealRepository.getFavoriteMeal(userEmail);
        List<FavoriteMeal> favorite = new ArrayList<>();
        favoriteMeal.observeForever(favoriteMeals -> {
            if (favoriteMeals != null && !favoriteMeals.isEmpty()) {
                favorite.addAll(favoriteMeals);
                List<MealDTO> allMeals = new ArrayList<>();
                List<MealMeasureIngredient> allMealMeasureIngredients = new ArrayList<>();
                AtomicInteger remainingMeals = new AtomicInteger(favoriteMeals.size());

                for (FavoriteMeal favoriteMeal : favoriteMeals) {
                    mealDTO = mealRepository.getMealById(favoriteMeal.getMealId());
                    mealDTO.observeForever(meal -> {
                        if (meal != null) {
                            allMeals.add(meal);
                            LiveData<List<MealMeasureIngredient>> mealMeasureIngredientsLiveData =
                                    mealRepository.getIngredientsByMealId(meal.getIdMeal());

                            mealMeasureIngredientsLiveData.observeForever(mealMeasureIngredients -> {
                                if (mealMeasureIngredients != null && !mealMeasureIngredients.isEmpty()) {
                                    allMealMeasureIngredients.addAll(mealMeasureIngredients);
                                }
                                if (remainingMeals.decrementAndGet() == 0) {
                                    performBackup(favorite, allMeals, allMealMeasureIngredients);
                                }
                            });
                        } else {
                            if (remainingMeals.decrementAndGet() == 0) {
                                performBackup(favorite, allMeals, allMealMeasureIngredients);
                            }
                        }
                    });
                }
            } else {
                Log.e(TAG, "No favorite meals found to back up.");
            }
        });
    }



    private void performBackup(List<FavoriteMeal> favoriteMeals, List<MealDTO> meals, List<MealMeasureIngredient> mealMeasureIngredients) {
        try {
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (currentUserEmail == null) {
                Log.e(TAG, "User not authenticated");
                return;
            }

            Gson gson = new Gson();
            Map<String, Object> favoriteMealsData = new HashMap<>();
            favoriteMealsData.put("favoriteMeals", gson.toJson(favoriteMeals));

            Map<String, Object> mealsData = new HashMap<>();
            mealsData.put("meals", gson.toJson(meals));

            Map<String, Object> ingredientsData = new HashMap<>();
            ingredientsData.put("ingredients", gson.toJson(mealMeasureIngredients));

            db.collection("favoriteMeals").document(currentUserEmail).collection("backup").document("favoriteMeals")
                    .set(favoriteMealsData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Favorite meals backup successful"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up favorite meals", e));

            db.collection("Meals").document(currentUserEmail).collection("backup").document("meals")
                    .set(mealsData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Meals backup successful"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up meals", e));

            db.collection("ingredients").document(currentUserEmail).collection("backup").document("ingredients")
                    .set(ingredientsData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Ingredients backup successful"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up ingredients", e));

        } catch (Exception e) {
            Log.e(TAG, "Failed to backup data", e);
        }
    }

    @Override
    public void restoreData(String currentUserEmail) {
        try {

            if (currentUserEmail == null) {
                Log.e(TAG, "User not authenticated");
                return;
            }

            Gson gson = new Gson();

            // Restore favoriteMeals
            db.collection("favoriteMeals").document(currentUserEmail).collection("backup").document("favoriteMeals")
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String favoriteMealsJson = (String) documentSnapshot.get("favoriteMeals");
                            Type favoriteMealsType = new TypeToken<List<FavoriteMeal>>() {}.getType();
                            List<FavoriteMeal> favoriteMeals = gson.fromJson(favoriteMealsJson, favoriteMealsType);
                            // Insert the restored favoriteMeals into the local database
                            mealRepository.insertAllFavoriteMeals(favoriteMeals);
                            Log.d(TAG, "Favorite meals restored and inserted successfully");
                        } else {
                            Log.d(TAG, "No favorite meals backup found");
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error restoring favorite meals", e));

            // Restore meals
            db.collection("Meals").document(currentUserEmail).collection("backup").document("meals")
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String mealsJson = (String) documentSnapshot.get("meals");
                            Type mealsType = new TypeToken<List<MealDTO>>() {}.getType();
                            List<MealDTO> meals = gson.fromJson(mealsJson, mealsType);
                            // Insert the restored meals into the local database
                            mealRepository.insertAllMeals(meals);
                            Log.d(TAG, "Meals restored and inserted successfully");
                        } else {
                            Log.d(TAG, "No meals backup found");
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error restoring meals", e));

            // Restore ingredients
            db.collection("ingredients").document(currentUserEmail).collection("backup").document("ingredients")
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String ingredientsJson = (String) documentSnapshot.get("ingredients");
                            Type ingredientsType = new TypeToken<List<MealMeasureIngredient>>() {}.getType();
                            List<MealMeasureIngredient> ingredients = gson.fromJson(ingredientsJson, ingredientsType);
                            // Insert the restored ingredients into the local database
                            mealRepository.insertAllIngredients(ingredients);
                            Log.d(TAG, "Ingredients restored and inserted successfully");
                        } else {
                            Log.d(TAG, "No ingredients backup found");
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error restoring ingredients", e));

        } catch (Exception e) {
            Log.e(TAG, "Failed to restore data", e);
        }
    }

    public void backupData2(String userEmail) {
        mealPlan = mealRepository.getMealPlans(userEmail);
        List<MealPlan> plan = new ArrayList<>();
        mealPlan.observeForever(planMeals -> {
            if (planMeals != null && !planMeals.isEmpty()) {
                plan.addAll(planMeals);
                List<MealDTO> allMeals = new ArrayList<>();
                List<MealMeasureIngredient> allMealMeasureIngredients = new ArrayList<>();
                AtomicInteger remainingMeals = new AtomicInteger(planMeals.size());

                for (MealPlan mealPlan : planMeals) {
                    mealDTO = mealRepository.getMealById(mealPlan.getMealId());
                    mealDTO.observeForever(meal -> {
                        if (meal != null) {
                            allMeals.add(meal);
                            LiveData<List<MealMeasureIngredient>> mealMeasureIngredientsLiveData =
                                    mealRepository.getIngredientsByMealId(meal.getIdMeal());

                            mealMeasureIngredientsLiveData.observeForever(mealMeasureIngredients -> {
                                if (mealMeasureIngredients != null && !mealMeasureIngredients.isEmpty()) {
                                    allMealMeasureIngredients.addAll(mealMeasureIngredients);
                                }
                                if (remainingMeals.decrementAndGet() == 0) {
                                    performBackup2(plan, allMeals, allMealMeasureIngredients);
                                }
                            });
                        } else {
                            if (remainingMeals.decrementAndGet() == 0) {
                                performBackup2(plan, allMeals, allMealMeasureIngredients);
                            }
                        }
                    });
                }
            } else {
                Log.e(TAG, "No favorite meals found to back up.");
            }
        });
    }


    private void performBackup2(List<MealPlan> mealPlans, List<MealDTO> meals, List<MealMeasureIngredient> mealMeasureIngredients) {
        try {
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (currentUserEmail == null) {
                Log.e(TAG, "User not authenticated");
                return;
            }

            Gson gson = new Gson();
            Map<String, Object> planMealsData = new HashMap<>();
            planMealsData.put("mealPlans", gson.toJson(mealPlans));

            Map<String, Object> mealsData = new HashMap<>();
            mealsData.put("meals", gson.toJson(meals));

            Map<String, Object> ingredientsData = new HashMap<>();
            ingredientsData.put("ingredients", gson.toJson(mealMeasureIngredients));

            db.collection("palnMeals").document(currentUserEmail).collection("backup").document("planMeals")
                    .set(planMealsData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Plan meals backup successful"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up Plan meals", e));

            db.collection("Meals").document(currentUserEmail).collection("backup").document("meals")
                    .set(mealsData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Meals backup successful"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up meals", e));

            db.collection("ingredients").document(currentUserEmail).collection("backup").document("ingredients")
                    .set(ingredientsData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Ingredients backup successful"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up ingredients", e));

        } catch (Exception e) {
            Log.e(TAG, "Failed to backup data", e);
        }
    }

    public void restoreData2(String userEmail) {
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (currentUserEmail == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        // Restore Meal Plans
        db.collection("palnMeals").document(currentUserEmail).collection("backup").document("favoriteMeals")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String mealPlansJson = documentSnapshot.getString("mealPlans");
                        Type mealPlanType = new TypeToken<List<MealPlan>>() {}.getType();
                        List<MealPlan> mealPlans = new Gson().fromJson(mealPlansJson, mealPlanType);

                        // Restore Meals
                        db.collection("Meals").document(currentUserEmail).collection("backup").document("meals")
                                .get()
                                .addOnSuccessListener(documentSnapshot1 -> {
                                    if (documentSnapshot1.exists()) {
                                        String mealsJson = documentSnapshot1.getString("meals");
                                        Type mealType = new TypeToken<List<MealDTO>>() {}.getType();
                                        List<MealDTO> meals = new Gson().fromJson(mealsJson, mealType);

                                        // Restore Ingredients
                                        db.collection("ingredients").document(currentUserEmail).collection("backup").document("ingredients")
                                                .get()
                                                .addOnSuccessListener(documentSnapshot2 -> {
                                                    if (documentSnapshot2.exists()) {
                                                        String ingredientsJson = documentSnapshot2.getString("ingredients");
                                                        Type ingredientType = new TypeToken<List<MealMeasureIngredient>>() {}.getType();
                                                        List<MealMeasureIngredient> ingredients = new Gson().fromJson(ingredientsJson, ingredientType);

                                                        // Update local repository
                                                        updateLocalRepository(mealPlans, meals, ingredients);
                                                    } else {
                                                        Log.e(TAG, "Ingredients data not found.");
                                                    }
                                                })
                                                .addOnFailureListener(e -> Log.e(TAG, "Error restoring ingredients", e));
                                    } else {
                                        Log.e(TAG, "Meals data not found.");
                                    }
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error restoring meals", e));
                    } else {
                        Log.e(TAG, "Meal plans data not found.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error restoring meal plans", e));
    }

    private void updateLocalRepository(List<MealPlan> mealPlans, List<MealDTO> meals, List<MealMeasureIngredient> ingredients) {
        // Here you should implement the logic to update your local database with the fetched data
        // For example:
        mealRepository.insertAllPlanMeals(mealPlans);
        mealRepository.insertAllMeals(meals);
        mealRepository.insertAllIngredients(ingredients);
    }


}


