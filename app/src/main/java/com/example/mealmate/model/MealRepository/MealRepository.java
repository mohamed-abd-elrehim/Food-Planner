package com.example.mealmate.model.MealRepository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealWithDetails;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMealWithMeals;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlanWithMeals;
import com.example.mealmate.model.network.network_Interface.NetworkCallback;
import com.example.mealmate.model.network.RemoteDataSourceImpl;

import java.util.Collections;
import java.util.List;

public class MealRepository implements MealRepositoryInterface {

    private static MealRepository repository = null;
    LocalDataSourceImpl localDataSource;
    RemoteDataSourceImpl remoteDataSource;
    private final String TAG = "MealRepository";

    private MealRepository(LocalDataSourceImpl localDataSource, RemoteDataSourceImpl remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public static MealRepository getInstance(LocalDataSourceImpl localDataSource, RemoteDataSourceImpl remoteDataSource) {
        if (repository == null) {
            repository = new MealRepository(localDataSource, remoteDataSource);
        }
        return repository;
    }


    @Override
    public void makeNetworkCallback(NetworkCallback networkCallback, String endpoint, String... params) {
        remoteDataSource.makeNetworkCallback(networkCallback, endpoint, params);
    }

    @Override
    public void updateBaseUrl(String newBaseUrl) {
        remoteDataSource.updateBaseUrl(newBaseUrl);
    }

    @Override
    public void insertFavoriteMealWithMeals(FavoriteMeal meal, MealDTO mealDTO, List<MealMeasureIngredient> ingredients) {
        localDataSource.insertFavoriteMealWithMeals(meal, mealDTO, ingredients);
    }

    @Override
    public LiveData<List<FavoriteMeal>> getFavoriteMeal(String clientEmail) {
        return localDataSource.getFavoriteMeal(clientEmail);
    }


    @Override
    public void insertMealPlan(MealPlan mealPlan, MealDTO meal, List<MealMeasureIngredient> ingredients) {
        localDataSource.insertMealPlan(mealPlan, meal, ingredients);
    }

    @Override
    public LiveData<List<MealPlan>> getMealPlans(String clientEmail) {
        return localDataSource.getMealPlans(clientEmail);
    }

    @Override
    public LiveData<MealDTO> getMealById(String mealId) {
        return localDataSource.getMealById(mealId);
    }

    @Override
    public LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId) {
        return localDataSource.getIngredientsByMealId(mealId);
    }

    @Override
    public void deleteMealPlan(MealPlan mealPlan) {
        localDataSource.deleteMealPlan(mealPlan);
    }

    @Override
    public void insertAllPlanMeals(List<MealPlan> mealPlans) {
        localDataSource.insertAllPlanMeals(mealPlans);
    }

    @Override
    public List<MealPlan> getPlanMealsForUserSync(String email) {
        return localDataSource.getPlanMealsForUserSync(email);
    }

    @Override
    public void deletePlanMeal(MealPlan mealPlan) {
        localDataSource.deletePlanMeal(mealPlan);
    }

    @Override
    public void deleteFavoriteMeal(FavoriteMeal favoriteMeal) {
        localDataSource.deleteFavoriteMeal(favoriteMeal);
    }

    @Override
    public void insertAllFavoriteMeals(List<FavoriteMeal> favoriteMeals) {
        localDataSource.insertAllFavoriteMeals(favoriteMeals);
    }

    @Override
    public void insertAllMeals(List<MealDTO> meals) {
        localDataSource.insertAllMeals(meals);
    }

    @Override
    public void insertAllIngredients(List<MealMeasureIngredient> ingredients) {
        localDataSource.insertAllIngredients(ingredients);
    }

    @Override
    public List<FavoriteMeal> getFavoriteMealsForUserSync(String email) {
        return localDataSource.getFavoriteMealsForUserSync(email);
    }

    @Override
    public void deleteFavorite(FavoriteMeal favoriteMeal) {
        localDataSource.deleteFavorite(favoriteMeal);
    }


}
