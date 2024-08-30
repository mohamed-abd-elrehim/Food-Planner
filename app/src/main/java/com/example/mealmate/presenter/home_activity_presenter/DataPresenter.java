package com.example.mealmate.presenter.home_activity_presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.presenter.home_activity_presenter.home_activity_presenter_interface.DataRepository;
import com.example.mealmate.veiw.home_activity.home_activity_interface.Home_Activity_Interface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DataPresenter implements DataRepository {
    private static final String TAG = "DataPresenter";

    private final MealRepository mealRepository;
    private final FirebaseFirestore db;
    private final Home_Activity_Interface view;

    private LiveData<List<FavoriteMeal>> favoriteMealsLiveData;
    private LiveData<List<MealPlan>> mealPlansLiveData;
    private String currentUserEmail;

    public DataPresenter(MealRepository mealRepository, Home_Activity_Interface view) {
        this.mealRepository = mealRepository;
        this.view = view;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void backupData() {
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (currentUserEmail == null) {
            Log.e(TAG, "User not authenticated");
            view.onFailure("User not authenticated");
            return;
        }

        favoriteMealsLiveData = mealRepository.getFavoriteMeal(currentUserEmail);
        mealPlansLiveData = mealRepository.getMealPlans(currentUserEmail);

        final List<FavoriteMeal> listFavoriteMeal = new ArrayList<>();
        final List<MealPlan> listMealPlans = new ArrayList<>();

        favoriteMealsLiveData.observeForever(favoriteMeals -> {
            if (favoriteMeals != null && !favoriteMeals.isEmpty()) {
                listFavoriteMeal.addAll(favoriteMeals);
            } else {
                Log.e(TAG, "No favorite meals found to back up.");
                view.onFailure("No favorite meals found to back up.");
            }
        });

        mealPlansLiveData.observeForever(mealPlans -> {
            if (mealPlans != null && !mealPlans.isEmpty()) {
                listMealPlans.addAll(mealPlans);
            } else {
                Log.e(TAG, "No meal plans found to back up.");
                view.onFailure("No meal plans found to back up.");
            }
        });

        // Delay to allow data fetching
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!listFavoriteMeal.isEmpty() || !listMealPlans.isEmpty()) {
                performBackupProcess(listFavoriteMeal, listMealPlans);
            } else {
                Log.e(TAG, "No data to backup.");
                view.onFailure("No data to backup.");
            }
        }, 1000); // Adjust delay as necessary
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
        LiveData<MealDTO> mealDTO = mealRepository.getMealById(mealId);
        mealDTO.observeForever(meal -> {
            if (meal != null) {
                allMeals.add(meal);
                LiveData<List<MealMeasureIngredient>> mealMeasureIngredientLiveData = mealRepository.getIngredientsByMealId(meal.getIdMeal());
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
                view.onFailure("User not authenticated");
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
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Favorite meals backup successful");
                            view.onSuccess("Favorite meals backup successful");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error backing up favorite meals", e);
                            view.onFailure("Error backing up favorite meals");
                        });
            }

            if (mealPlans != null) {
                Map<String, Object> mealPlansData = new HashMap<>();
                mealPlansData.put("mealPlans", gson.toJson(mealPlans));

                parentCollection.document("mealPlans")
                        .set(mealPlansData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Meal plans backup successful");
                            view.onSuccess("Meal plans backup successful");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error backing up meal plans", e);
                            view.onFailure("Error backing up meal plans");
                        });
            }

            if (meals != null) {
                Map<String, Object> mealsData = new HashMap<>();
                mealsData.put("meals", gson.toJson(meals));

                parentCollection.document("meals")
                        .set(mealsData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Meals backup successful");
                            view.onSuccess("Meals backup successful");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error backing up meals", e);
                            view.onFailure("Error backing up meals");
                        });
            }

            if (mealMeasureIngredients != null) {
                Map<String, Object> ingredientsData = new HashMap<>();
                ingredientsData.put("ingredients", gson.toJson(mealMeasureIngredients));

                parentCollection.document("ingredients")
                        .set(ingredientsData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Ingredients backup successful");
                            view.onSuccess("Ingredients backup successful");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error backing up ingredients", e);
                            view.onFailure("Error backing up ingredients");
                        });
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to backup data", e);
            view.onFailure("Failed to backup data");
        }
    }

    public void restoreData() {
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (currentUserEmail == null) {
            Log.e(TAG, "User not authenticated");
            view.onFailure("User not authenticated");
            return;
        }

        // Fetch backup data from Firestore subcollection
        db.collection("backupDataCollection").document(currentUserEmail).collection("backup").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<FavoriteMeal> favoriteMeals = null;
                    List<MealPlan> mealPlans = null;
                    List<MealDTO> meals = null;
                    List<MealMeasureIngredient> mealMeasureIngredients = null;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String documentId = document.getId();
                        String jsonData = (String) document.get(documentId);

                        try {
                            Gson gson = new Gson();
                            switch (documentId) {
                                case "favoriteMeals":
                                    Type favoriteMealsListType = new TypeToken<List<FavoriteMeal>>() {
                                    }.getType();
                                    favoriteMeals = gson.fromJson(jsonData, favoriteMealsListType);
                                    break;

                                case "mealPlans":
                                    Type mealPlansListType = new TypeToken<List<MealPlan>>() {
                                    }.getType();
                                    mealPlans = gson.fromJson(jsonData, mealPlansListType);
                                    break;

                                case "meals":
                                    Type mealsListType = new TypeToken<List<MealDTO>>() {
                                    }.getType();
                                    meals = gson.fromJson(jsonData, mealsListType);
                                    break;

                                case "ingredients":
                                    Type mealMeasureIngredientsListType = new TypeToken<List<MealMeasureIngredient>>() {
                                    }.getType();
                                    mealMeasureIngredients = gson.fromJson(jsonData, mealMeasureIngredientsListType);
                                    break;

                                default:
                                    Log.e(TAG, "Unknown document ID: " + documentId);
                                    view.onFailure("Unknown document ID: " + documentId);
                                    break;
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "JSON parsing error: ", e);
                            view.onFailure("JSON parsing error: " + e.getMessage());
                        }
                    }

                    restoreDataToDatabase(favoriteMeals, mealPlans, meals, mealMeasureIngredients);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to restore data", e);
                    view.onFailure("Failed to restore data");
                });
    }

    private void restoreDataToDatabase(List<FavoriteMeal> favoriteMeals, List<MealPlan> mealPlans,
                                       List<MealDTO> meals, List<MealMeasureIngredient> mealMeasureIngredients) {
        if (favoriteMeals != null) {
            mealRepository.insertAllFavoriteMeals(favoriteMeals);
            view.onSuccess("Favorite meals restored successfully");
        }

        if (mealPlans != null) {
            mealRepository.insertAllPlanMeals(mealPlans);
            view.onSuccess("Meal plans restored successfully");
        }

        if (meals != null) {
                mealRepository.insertAllMeals(meals);
                view.onSuccess("Meals restored successfully");
        }

        if (mealMeasureIngredients != null) {
            mealRepository.insertAllIngredients(mealMeasureIngredients);
            view.onSuccess("Ingredients restored successfully");
        }
    }
}
