package com.example.mealmate.model.MealRepository;

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


//    @Override
//    public LiveData<List<FavoriteMealWithMeals>> getFavoritesWithMeals(String clientEmail) {
//        return localDataSource.getFavoritesWithMeals(clientEmail);
//    }
//
//    @Override
//    public LiveData<FavoriteMeal> getFavoriteMeal(String mealId, String clientEmail) {
//        return localDataSource.getFavoriteMeal(mealId, clientEmail);
//    }
//
//    @Override
//    public void insertFavoriteMeal(FavoriteMeal favoriteMeal) {
//        localDataSource.insertFavoriteMeal(favoriteMeal);
//    }
//
//    @Override
//    public void deleteFavoriteMeal(FavoriteMeal favoriteMeal) {
//        localDataSource.deleteFavoriteMeal(favoriteMeal);
//    }
//
//    @Override
//    public LiveData<List<MealDTO>> getAllMeals() {
//
//        return localDataSource.getAllMeals();
//    }
//
//    @Override
//    public LiveData<MealWithDetails> getMealWithIngredients(String mealId) {
//        return localDataSource.getMealWithIngredients(mealId);
//    }
//
//    @Override
//    public LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId) {
//        return localDataSource.getIngredientsByMealId(mealId);
//    }
//
//    @Override
//    public void insertMeal(MealDTO meal) {
//        localDataSource.insertMeal(meal);
//
//    }
//
//    @Override
//    public void deleteMeal(MealDTO meal) {
//        localDataSource.deleteMeal(meal);
//    }
//
//    @Override
//    public LiveData<List<MealPlanWithMeals>> getMealPlansWithMeals(String clientEmail) {
//        return localDataSource.getMealPlansWithMeals(clientEmail);
//    }
//
//    @Override
//    public LiveData<MealPlan> getMealPlan(String mealId, String clientEmail) {
//        return localDataSource.getMealPlan(mealId, clientEmail);
//    }
//
//    @Override
//    public void insertMealPlan(MealPlan mealPlan) {
//        localDataSource.insertMealPlan(mealPlan);
//    }
//
//    @Override
//    public void deleteMealPlan(MealPlan mealPlan) {
//        localDataSource.deleteMealPlan(mealPlan);
//
//    }

    @Override
    public void makeNetworkCallback(NetworkCallback networkCallback, String endpoint, String... params) {
        remoteDataSource.makeNetworkCallback(networkCallback, endpoint, params);
    }

    @Override
    public void updateBaseUrl(String newBaseUrl) {
        remoteDataSource.updateBaseUrl(newBaseUrl);
    }

    @Override
    public void insertFavoriteMealWithMeals(FavoriteMeal meal, MealDTO mealDTO ,List<MealMeasureIngredient> ingredients) {
        localDataSource.insertFavoriteMealWithMeals(meal, mealDTO, ingredients);
    }

    @Override
    public void insertMealWithDetails(MealDTO meal, List<MealMeasureIngredient> ingredients) {
        localDataSource.insertMealWithDetails(meal, ingredients);
    }
}
