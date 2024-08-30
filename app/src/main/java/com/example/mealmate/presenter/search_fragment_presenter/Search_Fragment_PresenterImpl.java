package com.example.mealmate.presenter.search_fragment_presenter;

import android.util.Log;

import com.example.mealmate.model.MealArea;
import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealIngredient;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.network.MealAreaResponse;
import com.example.mealmate.model.network.MealCategoryResponse;
import com.example.mealmate.model.network.MealIngredientResponse;
import com.example.mealmate.model.network.MealResponse;
import com.example.mealmate.model.network.network_Interface.NetworkCallback;
import com.example.mealmate.presenter.search_fragment_presenter.search_fragment_presenter_interface.Search_Fragment_Presenter_Interface;
import com.example.mealmate.veiw.search_fragment.search_fragment_veiw_interface.Search_Fragment_Veiw_Interface;

import java.util.List;

import retrofit2.Response;

public class Search_Fragment_PresenterImpl implements Search_Fragment_Presenter_Interface, NetworkCallback {

    private Search_Fragment_Veiw_Interface view;
    public static final String TAG = "Search_Fragment_PresenterImpl";
    MealRepository mealRepository;

    public Search_Fragment_PresenterImpl( MealRepository mealRepository, Search_Fragment_Veiw_Interface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }



    @Override
    public void loadAllCategoriess() {
        mealRepository.makeNetworkCallback(this, "listAllCategories");

    }

    @Override
    public void loadAllIngredient() {
        mealRepository.makeNetworkCallback(this, "listAllIngredients");

    }

    @Override
    public void loadAllArea() {
        mealRepository.makeNetworkCallback(this, "listAllAreas");

    }

    @Override
    public void loadFilteredCategoriess(String categoryName) {
        mealRepository.makeNetworkCallback(this, "filterByCategory", categoryName);
        Log.i(TAG, "loadFilteredCategoriess: ");
    }

    @Override
    public void loadFilteredIngredient(String ingredientName) {
        mealRepository.makeNetworkCallback(this, "filterByIngredient", ingredientName);
    }

    @Override
    public void loadFilteredArea(String areaName) {
        mealRepository.makeNetworkCallback(this, "filterByArea", areaName);

    }

    @Override
    public void loadFilteredByName(String name) {
        mealRepository.makeNetworkCallback(this, "searchMealByName", name);

    }

    @Override
    public void seeMore(String Id) {

    }

    @Override
    public void onSuccessResult(Response response) {
        if (response.isSuccessful()) {
            Object body = response.body();
            if (body instanceof MealCategoryResponse) {
                // Handle MealCategoryResponse (CategoriesResponse)
                MealCategoryResponse categoriesResponse = (MealCategoryResponse) body;
                List<MealCategory> categories = categoriesResponse.getCategories();
                if (categories != null && !categories.isEmpty()) {
                    Log.i(TAG, "onSuccessResult: " + categories.size());
                    view.showData(categories);
                    Log.i(TAG, "Categories loaded successfully: " + categories.size() + " categories found.");
                } else {
                    view.showError("No categories found.");
                    Log.w(TAG, "Categories response was successful but no categories were found.");
                }
            } else if (body instanceof MealIngredientResponse) {
                // Handle MealCategoryResponse (CategoriesResponse)
                MealIngredientResponse ingredientResponse = (MealIngredientResponse) body;
                List<MealIngredient> ingredients = ingredientResponse.getIngredients();
                if (ingredients != null && !ingredients.isEmpty()) {
                    Log.i(TAG, "onSuccessResult: " + ingredients.size());
                    view.showData(ingredients);
                    Log.i(TAG, "Ingredients loaded successfully: " + ingredients.size() + " Ingredients found.");
                } else {
                    view.showError("No ingredients found.");
                    Log.w(TAG, "Ingredients response was successful but no ingredients were found.");
                }
            } else if (body instanceof MealResponse) {
                // Handle MealResponse (RandomMealResponse)
                MealResponse mealResponse = (MealResponse) body;
                List<MealDTO> meals = mealResponse.getMeals();
                if (meals != null && !meals.isEmpty()) {
                    view.showData(meals);
                    Log.i(TAG, "Meals loaded successfully: " + meals.size() + " meals found.");
                } else {
                    view.showError("No meals found.");
                    Log.w(TAG, "Meals response was successful but no meals were found.");
                }
            } else if (body instanceof MealAreaResponse) {
                // Handle MealCategoryResponse (CategoriesResponse)
                MealAreaResponse areaResponse = (MealAreaResponse) body;
                List<MealArea> areas = areaResponse.getMealAreas();
                if (areas != null && !areas.isEmpty()) {
                    Log.i(TAG, "onSuccessResult: " + areas.size());
                    view.showData(areas);
                    Log.i(TAG, "Areas loaded successfully: " + areas.size() + " Areas found.");
                } else {
                    view.showError("No Areas found.");
                    Log.w(TAG, "Areas response was successful but no Areas were found.");
                }
            }else {
                view.showError("Unexpected response type.");
                Log.e(TAG, "Unexpected response type: " + body.getClass().getSimpleName());
            }
        } else {
            view.showError("Request failed with status code: " + response.code());
            Log.e(TAG, "Request failed with status code: " + response.code());
        }
    }

    @Override
    public void onFailureResult(String errorMsg) {
        view.showError("Failed to load data: " + errorMsg);
        Log.e(TAG, "Error loading data: " + errorMsg);
    }
}
