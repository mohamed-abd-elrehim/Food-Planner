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
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();


    }



    @Override
    public void backupData() {
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


//
//    @Override
//    public void backupData(String userEmail) {
//        favoriteMeal = mealRepository.getFavoriteMeal(userEmail);
//        List<FavoriteMeal> favorite = new ArrayList<>();
//        favoriteMeal.observeForever(favoriteMeals -> {
//            if (favoriteMeals != null && !favoriteMeals.isEmpty()) {
//                favorite.addAll(favoriteMeals);
//                List<MealDTO> allMeals = new ArrayList<>();
//                List<MealMeasureIngredient> allMealMeasureIngredients = new ArrayList<>();
//                AtomicInteger remainingMeals = new AtomicInteger(favoriteMeals.size());
//
//                for (FavoriteMeal favoriteMeal : favoriteMeals) {
//                    mealDTO = mealRepository.getMealById(favoriteMeal.getMealId());
//                    mealDTO.observeForever(meal -> {
//                        if (meal != null) {
//                            allMeals.add(meal);
//                            LiveData<List<MealMeasureIngredient>> mealMeasureIngredientsLiveData =
//                                    mealRepository.getIngredientsByMealId(meal.getIdMeal());
//
//                            mealMeasureIngredientsLiveData.observeForever(mealMeasureIngredients -> {
//                                if (mealMeasureIngredients != null && !mealMeasureIngredients.isEmpty()) {
//                                    allMealMeasureIngredients.addAll(mealMeasureIngredients);
//                                }
//                                if (remainingMeals.decrementAndGet() == 0) {
//                                    performBackup(favorite, allMeals, allMealMeasureIngredients);
//                                }
//                            });
//                        } else {
//                            if (remainingMeals.decrementAndGet() == 0) {
//                                performBackup(favorite, allMeals, allMealMeasureIngredients);
//                            }
//                        }
//                    });
//                }
//            } else {
//                Log.e(TAG, "No favorite meals found to back up.");
//            }
//        });
//    }
//    public void backupData2(String userEmail) {
//        mealPlan = mealRepository.getMealPlans(userEmail);
//        List<MealPlan> plan = new ArrayList<>();
//        mealPlan.observeForever(planMeals -> {
//            if (planMeals != null && !planMeals.isEmpty()) {
//                plan.addAll(planMeals);
//                List<MealDTO> allMeals = new ArrayList<>();
//                List<MealMeasureIngredient> allMealMeasureIngredients = new ArrayList<>();
//                AtomicInteger remainingMeals = new AtomicInteger(planMeals.size());
//
//                for (MealPlan mealPlan : planMeals) {
//                    mealDTO = mealRepository.getMealById(mealPlan.getMealId());
//                    mealDTO.observeForever(meal -> {
//                        if (meal != null) {
//                            allMeals.add(meal);
//                            LiveData<List<MealMeasureIngredient>> mealMeasureIngredientsLiveData =
//                                    mealRepository.getIngredientsByMealId(meal.getIdMeal());
//
//                            mealMeasureIngredientsLiveData.observeForever(mealMeasureIngredients -> {
//                                if (mealMeasureIngredients != null && !mealMeasureIngredients.isEmpty()) {
//                                    allMealMeasureIngredients.addAll(mealMeasureIngredients);
//                                }
//                                if (remainingMeals.decrementAndGet() == 0) {
//                                    performBackup2(plan, allMeals, allMealMeasureIngredients);
//                                }
//                            });
//                        } else {
//                            if (remainingMeals.decrementAndGet() == 0) {
//                                performBackup2(plan, allMeals, allMealMeasureIngredients);
//                            }
//                        }
//                    });
//                }
//            } else {
//                Log.e(TAG, "No favorite meals found to back up.");
//            }
//        });
//    }
//
//
//    private void performBackup(List<FavoriteMeal> favoriteMeals, List<MealDTO> meals, List<MealMeasureIngredient> mealMeasureIngredients) {
//        try {
//            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//            if (currentUserEmail == null) {
//                Log.e(TAG, "User not authenticated");
//                return;
//            }
//
//            Gson gson = new Gson();
//            Map<String, Object> favoriteMealsData = new HashMap<>();
//            favoriteMealsData.put("favoriteMeals", gson.toJson(favoriteMeals));
//
//            Map<String, Object> mealsData = new HashMap<>();
//            mealsData.put("meals", gson.toJson(meals));
//
//            Map<String, Object> ingredientsData = new HashMap<>();
//            ingredientsData.put("ingredients", gson.toJson(mealMeasureIngredients));
//
//            db.collection("favoriteMeals").document(currentUserEmail).collection("backup").document("favoriteMeals")
//                    .set(favoriteMealsData, SetOptions.merge())
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Favorite meals backup successful"))
//                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up favorite meals", e));
//
//            db.collection("Meals").document(currentUserEmail).collection("backup").document("meals")
//                    .set(mealsData, SetOptions.merge())
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Meals backup successful"))
//                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up meals", e));
//
//            db.collection("ingredients").document(currentUserEmail).collection("backup").document("ingredients")
//                    .set(ingredientsData, SetOptions.merge())
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Ingredients backup successful"))
//                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up ingredients", e));
//
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to backup data", e);
//        }
//    }
//
//    @Override
//    public void restoreData(String currentUserEmail) {
//        try {
//
//            if (currentUserEmail == null) {
//                Log.e(TAG, "User not authenticated");
//                return;
//            }
//
//            Gson gson = new Gson();
//
//            // Restore favoriteMeals
//            db.collection("favoriteMeals").document(currentUserEmail).collection("backup").document("favoriteMeals")
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            String favoriteMealsJson = (String) documentSnapshot.get("favoriteMeals");
//                            Type favoriteMealsType = new TypeToken<List<FavoriteMeal>>() {}.getType();
//                            List<FavoriteMeal> favoriteMeals = gson.fromJson(favoriteMealsJson, favoriteMealsType);
//                            // Insert the restored favoriteMeals into the local database
//                            mealRepository.insertAllFavoriteMeals(favoriteMeals);
//                            Log.d(TAG, "Favorite meals restored and inserted successfully");
//                        } else {
//                            Log.d(TAG, "No favorite meals backup found");
//                        }
//                    })
//                    .addOnFailureListener(e -> Log.e(TAG, "Error restoring favorite meals", e));
//
//            // Restore meals
//            db.collection("Meals").document(currentUserEmail).collection("backup").document("meals")
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            String mealsJson = (String) documentSnapshot.get("meals");
//                            Type mealsType = new TypeToken<List<MealDTO>>() {}.getType();
//                            List<MealDTO> meals = gson.fromJson(mealsJson, mealsType);
//                            // Insert the restored meals into the local database
//                            mealRepository.insertAllMeals(meals);
//                            Log.d(TAG, "Meals restored and inserted successfully");
//                        } else {
//                            Log.d(TAG, "No meals backup found");
//                        }
//                    })
//                    .addOnFailureListener(e -> Log.e(TAG, "Error restoring meals", e));
//
//            // Restore ingredients
//            db.collection("ingredients").document(currentUserEmail).collection("backup").document("ingredients")
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            String ingredientsJson = (String) documentSnapshot.get("ingredients");
//                            Type ingredientsType = new TypeToken<List<MealMeasureIngredient>>() {}.getType();
//                            List<MealMeasureIngredient> ingredients = gson.fromJson(ingredientsJson, ingredientsType);
//                            // Insert the restored ingredients into the local database
//                            mealRepository.insertAllIngredients(ingredients);
//                            Log.d(TAG, "Ingredients restored and inserted successfully");
//                        } else {
//                            Log.d(TAG, "No ingredients backup found");
//                        }
//                    })
//                    .addOnFailureListener(e -> Log.e(TAG, "Error restoring ingredients", e));
//
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to restore data", e);
//        }
//    }
//
//
//
//    private void performBackup2(List<MealPlan> mealPlans, List<MealDTO> meals, List<MealMeasureIngredient> mealMeasureIngredients) {
//        try {
//            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//            if (currentUserEmail == null) {
//                Log.e(TAG, "User not authenticated");
//                return;
//            }
//
//            Gson gson = new Gson();
//            Map<String, Object> planMealsData = new HashMap<>();
//            planMealsData.put("mealPlans", gson.toJson(mealPlans));
//
//            Map<String, Object> mealsData = new HashMap<>();
//            mealsData.put("meals", gson.toJson(meals));
//
//            Map<String, Object> ingredientsData = new HashMap<>();
//            ingredientsData.put("ingredients", gson.toJson(mealMeasureIngredients));
//
//            db.collection("palnMeals").document(currentUserEmail).collection("backup").document("planMeals")
//                    .set(planMealsData, SetOptions.merge())
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Plan meals backup successful"))
//                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up Plan meals", e));
//
//            db.collection("Meals").document(currentUserEmail).collection("backup").document("meals")
//                    .set(mealsData, SetOptions.merge())
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Meals backup successful"))
//                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up meals", e));
//
//            db.collection("ingredients").document(currentUserEmail).collection("backup").document("ingredients")
//                    .set(ingredientsData, SetOptions.merge())
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Ingredients backup successful"))
//                    .addOnFailureListener(e -> Log.e(TAG, "Error backing up ingredients", e));
//
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to backup data", e);
//        }
//    }
//
//    public void restoreData2(String userEmail) {
//        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//        if (currentUserEmail == null) {
//            Log.e(TAG, "User not authenticated");
//            return;
//        }
//
//        // Restore Meal Plans
//        db.collection("palnMeals").document(currentUserEmail).collection("backup").document("planMeals")
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        String mealPlansJson = documentSnapshot.getString("mealPlans");
//                        Type mealPlanType = new TypeToken<List<MealPlan>>() {}.getType();
//                        List<MealPlan> mealPlans = new Gson().fromJson(mealPlansJson, mealPlanType);
//
//                        // Restore Meals
//                        db.collection("Meals").document(currentUserEmail).collection("backup").document("meals")
//                                .get()
//                                .addOnSuccessListener(documentSnapshot1 -> {
//                                    if (documentSnapshot1.exists()) {
//                                        String mealsJson = documentSnapshot1.getString("meals");
//                                        Type mealType = new TypeToken<List<MealDTO>>() {}.getType();
//                                        List<MealDTO> meals = new Gson().fromJson(mealsJson, mealType);
//
//                                        // Restore Ingredients
//                                        db.collection("ingredients").document(currentUserEmail).collection("backup").document("ingredients")
//                                                .get()
//                                                .addOnSuccessListener(documentSnapshot2 -> {
//                                                    if (documentSnapshot2.exists()) {
//                                                        String ingredientsJson = documentSnapshot2.getString("ingredients");
//                                                        Type ingredientType = new TypeToken<List<MealMeasureIngredient>>() {}.getType();
//                                                        List<MealMeasureIngredient> ingredients = new Gson().fromJson(ingredientsJson, ingredientType);
//
//                                                        // Update local repository
//                                                        updateLocalRepository(mealPlans, meals, ingredients);
//                                                    } else {
//                                                        Log.e(TAG, "Ingredients data not found.");
//                                                    }
//                                                })
//                                                .addOnFailureListener(e -> Log.e(TAG, "Error restoring ingredients", e));
//                                    } else {
//                                        Log.e(TAG, "Meals data not found.");
//                                    }
//                                })
//                                .addOnFailureListener(e -> Log.e(TAG, "Error restoring meals", e));
//                    } else {
//                        Log.e(TAG, "Meal plans data not found.");
//                    }
//                })
//                .addOnFailureListener(e -> Log.e(TAG, "Error restoring meal plans", e));
//    }
//
//    private void updateLocalRepository(List<MealPlan> mealPlans, List<MealDTO> meals, List<MealMeasureIngredient> ingredients) {
//        // Here you should implement the logic to update your local database with the fetched data
//        // For example:
//        mealRepository.insertAllPlanMeals(mealPlans);
//        mealRepository.insertAllMeals(meals);
//        mealRepository.insertAllIngredients(ingredients);
//    }


//}



