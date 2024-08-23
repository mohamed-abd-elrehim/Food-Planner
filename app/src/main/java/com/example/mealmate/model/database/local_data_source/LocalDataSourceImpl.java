package com.example.mealmate.model.database.local_data_source;

import androidx.lifecycle.LiveData;

import com.example.mealmate.model.database.DAOs.FavoriteMealDAO;
import com.example.mealmate.model.database.DAOs.MealDAO;
import com.example.mealmate.model.database.DAOs.MealPlanDAO;
import com.example.mealmate.model.database.local_data_source.local_data_source_interface.FavoriteMealDataSource;
import com.example.mealmate.model.database.local_data_source.local_data_source_interface.LocalDataSource;
import com.example.mealmate.model.database.local_data_source.local_data_source_interface.MealDataSource;
import com.example.mealmate.model.database.local_data_source.local_data_source_interface.MealPlanDataSource;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealWithDetails;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMealWithMeals;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlanWithMeals;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalDataSourceImpl implements LocalDataSource {


    private final FavoriteMealDAO favoriteMealDAO;
    private final MealDAO mealDAO;
    private final MealPlanDAO mealPlanDAO;
    private final ExecutorService executorService;

    private static LocalDataSourceImpl localDataSource = null;
    private final String TAG = "ProductsLocalDataSource";


    private LocalDataSourceImpl(FavoriteMealDAO favoriteMealDAO, MealDAO mealDAO, MealPlanDAO mealPlanDAO) {
        this.favoriteMealDAO = favoriteMealDAO;
        this.mealDAO = mealDAO;
        this.mealPlanDAO = mealPlanDAO;
        this.executorService = Executors.newFixedThreadPool(6); // Adjust the number of threads as needed
    }

    public static LocalDataSourceImpl getInstance(FavoriteMealDAO favoriteMealDAO, MealDAO mealDAO, MealPlanDAO mealPlanDAO) {
        if (localDataSource == null) {
            localDataSource = new LocalDataSourceImpl(favoriteMealDAO, mealDAO, mealPlanDAO);
        }
        return localDataSource;
    }


    // Implementation of FavoriteMealDataSource
    @Override
    public void insertFavoriteMealWithMeals(FavoriteMeal meal, MealDTO mealDTO, List<MealMeasureIngredient> ingredients) {
        executorService.execute(() -> {
            favoriteMealDAO.insertFavoriteMeal(meal, mealDTO, ingredients);
        });

    }

    @Override
    public LiveData<MealDTO> getMealById(String mealId) {
        return favoriteMealDAO.getMealById(mealId);
    }

    @Override
    public LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId) {
        return favoriteMealDAO.getIngredientsByMealId(mealId);
    }

    @Override
    public LiveData<List<FavoriteMeal>> getFavoriteMeal(String clientEmail) {
        return favoriteMealDAO.getFavoriteMeal(clientEmail);
    }

    @Override
    public void deleteFavoriteMeal(FavoriteMeal favoriteMeal) {
        executorService.execute(() -> favoriteMealDAO.deleteFavoriteMeal(favoriteMeal));
    }

    @Override
    public void insertAllFavoriteMeals(List<FavoriteMeal> favoriteMeals) {
        executorService.execute(() -> favoriteMealDAO.insertAllFavoriteMeals(favoriteMeals));
    }

    @Override
    public void insertAllMeals(List<MealDTO> meals) {
        executorService.execute(() -> favoriteMealDAO.insertAllMeals(meals));
    }

    @Override
    public void insertAllIngredients(List<MealMeasureIngredient> ingredients) {
        executorService.execute(() -> favoriteMealDAO.insertAllIngredients(ingredients));
    }


    @Override
    public void insertMealPlan(MealPlan mealPlan, MealDTO meal, List<MealMeasureIngredient> ingredients) {
        executorService.execute(() -> mealPlanDAO.insertMealPlan(mealPlan, meal, ingredients));
    }

    @Override
    public LiveData<List<MealPlan>> getMealPlans(String clientEmail) {
        return mealPlanDAO.getMealPlans(clientEmail);
    }


    @Override
    public void deleteMealPlan(MealPlan mealPlan) {
        executorService.execute(() -> mealPlanDAO.deleteMealPlan(mealPlan));

    }

    @Override
    public void insertAllPlanMeals(List<MealPlan> mealPlans) {
        executorService.execute(() -> mealPlanDAO.insertAllPlanMeals(mealPlans));
    }


//    @Override
//    public LiveData<List<FavoriteMealWithMeals>> getFavoritesWithMeals(String clientEmail) {
//        return favoriteMealDAO.getFavoritesWithMeals(clientEmail);
//    }
//
//    @Override
//    public LiveData<FavoriteMeal> getFavoriteMeal(String mealId, String clientEmail) {
//        return favoriteMealDAO.getFavoriteMeal(mealId, clientEmail);
//    }
//
//    @Override
//    public void insertFavoriteMeal(final FavoriteMeal favoriteMeal) {
//        executorService.execute(() -> favoriteMealDAO.insertFavoriteMeal(favoriteMeal));
//    }
//
//    @Override
//    public void deleteFavoriteMeal(final FavoriteMeal favoriteMeal) {
//        executorService.execute(() -> favoriteMealDAO.deleteFavoriteMeal(favoriteMeal));
//    }
//
//    // Implementation of MealDataSource
//    @Override
//    public LiveData<List<MealDTO>> getAllMeals() {
//        return mealDAO.getAllMeals();
//    }
//
//    @Override
//    public LiveData<MealWithDetails> getMealWithIngredients(String mealId) {
//        return mealDAO.getMealWithIngredients(mealId);
//    }
//
//    @Override
//    public LiveData<List<MealMeasureIngredient>> getIngredientsByMealId(String mealId) {
//        return mealDAO.getIngredientsByMealId(mealId);
//    }
//
//    @Override
//    public void insertMeal(final MealDTO meal) {
//        executorService.execute(() -> mealDAO.insertMeal(meal));
//    }
//
//    @Override
//    public void deleteMeal(final MealDTO meal) {
//        executorService.execute(() -> mealDAO.deleteMeal(meal));
//    }
//
//    // Implementation of MealPlanDataSource
//    @Override
//    public LiveData<List<MealPlanWithMeals>> getMealPlansWithMeals(String clientEmail) {
//        return mealPlanDAO.getMealPlansWithMeals(clientEmail);
//    }
//
//    @Override
//    public LiveData<MealPlan> getMealPlan(String mealId, String clientEmail) {
//        return mealPlanDAO.getMealPlan(mealId, clientEmail);
//    }
//
//    @Override
//    public void insertMealPlan(final MealPlan mealPlan) {
//        executorService.execute(() -> mealPlanDAO.insertMealPlan(mealPlan));
//    }
//
//    @Override
//    public void deleteMealPlan(final MealPlan mealPlan) {
//        executorService.execute(() -> mealPlanDAO.deleteMealPlan(mealPlan));
//    }
//
//    @Override
//    public LiveData<List<MealPlan>> getAllMealPlans() {
//        return null;
//    }
//
//    @Override
//    public LiveData<MealPlanWithMeals> getMealPlanWithMeals(String mealPlanId) {
//        return null;
//    }
//
//    @Override
//    public void insertIngredients(List<MealMeasureIngredient> ingredients) {
//
//    }
//
//    @Override
//    public void deleteIngredientsByMealId(String mealId) {
//
//    }

    // Shutdown the ExecutorService when it's no longer needed (e.g., in the ViewModel's onCleared method)
    public void shutdownExecutorService() {
        executorService.shutdown();
    }


}
