package com.example.mealmate;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.network.CustomMealResponse;
import com.example.mealmate.model.network.network_Interface.NetworkCallback;
import com.example.mealmate.veiw.all_meal_details_fragment.all_meal_details_fragment_veiw_interface.AllMealDetailsFragment_Veiw_Interface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class AddPlanMealFragmentPresenter implements AddPlanMealFragmentPresenterInterFace, NetworkCallback {

    private AddPlanMealFragmentVeiwInterface view;
    public static final String TAG = "AllMealDetailsFragment_presenter";
    MealRepository mealRepository;
    AppDataBase appDataBase;


    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredient;
    private LiveData<List<CustomMeal>> customMeals;

    public AddPlanMealFragmentPresenter(AppDataBase appDataBase, MealRepository mealRepository, AddPlanMealFragmentVeiwInterface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.appDataBase = appDataBase;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }



    @Override
    public void addMealToPaln(MealPlan mealPlan, CustomMeal meal) {
        List<MealMeasureIngredient> mealMeasureIngredient = getMealMeasureIngredients(meal);
        MealDTO mealDTO = new MealDTO(
                meal.getStrCategory(),                // String
                meal.getStrImageSource(),             // String
                meal.getStrCreativeCommonsConfirmed(),// String
                meal.getDateModified(),               // String
                meal.getIdMeal(),                     // String
                meal.getStrMeal(),                    // String
                meal.getStrDrinkAlternate(),          // String
                meal.getStrArea(),                    // String
                meal.getStrInstructions(),            // String
                meal.getStrMealThumb(),               // String
                meal.getStrTags(),                    // String
                meal.getStrYoutube(),                 // String
                meal.getStrSource()                   // String
        );
        mealRepository.insertMealPlan(mealPlan, mealDTO, mealMeasureIngredient);

    }

    @Override
    public void loadAllMealDetailsById(String id) {
        mealRepository.makeNetworkCallback(this, "lookupAllMealDitailsById", id);

    }


    @Override
    public void onSuccessResult(Response response) {
        if (response.isSuccessful()) {
            Object body = response.body();
            if (body instanceof CustomMealResponse) {
                // Handle MealCategoryResponse (CategoriesResponse)
                CustomMealResponse customMealResponse = (CustomMealResponse) body;
                List<CustomMeal> customMeals = customMealResponse.getCustomMeals();
                if (customMeals != null && !customMeals.isEmpty()) {
                    Log.i(TAG, "onSuccessResult: " + customMeals.size());
                    view.showData(customMeals);
                    Log.i(TAG, "Meals loaded successfully: " + customMeals.size() + " Meals found.");
                } else {
                    view.showError("No Meals found.");
                    Log.w(TAG, "Meals response was successful but no Meals were found.");
                }
            }
        }
    }

    @Override
    public void onFailureResult(String errorMsg) {
        view.showError("Failed to load data: " + errorMsg);
        Log.e(TAG, "Error loading data: " + errorMsg);

    }


    private List<MealMeasureIngredient> getMealMeasureIngredients(CustomMeal meal) {
        List<MealMeasureIngredient> ingredientsList = new ArrayList<>();

        // Array of ingredient fields and corresponding measure fields
        String[] ingredients = {
                meal.getStrIngredient1(), meal.getStrIngredient2(), meal.getStrIngredient3(),
                meal.getStrIngredient4(), meal.getStrIngredient5(), meal.getStrIngredient6(),
                meal.getStrIngredient7(), meal.getStrIngredient8(), meal.getStrIngredient9(),
                meal.getStrIngredient10(), meal.getStrIngredient11(), meal.getStrIngredient12(),
                meal.getStrIngredient13(), meal.getStrIngredient14(), meal.getStrIngredient15(),
                meal.getStrIngredient16(), meal.getStrIngredient17(), meal.getStrIngredient18(),
                meal.getStrIngredient19(), meal.getStrIngredient20()
        };

        String[] measures = {
                meal.getStrMeasure1(), meal.getStrMeasure2(), meal.getStrMeasure3(),
                meal.getStrMeasure4(), meal.getStrMeasure5(), meal.getStrMeasure6(),
                meal.getStrMeasure7(), meal.getStrMeasure8(), meal.getStrMeasure9(),
                meal.getStrMeasure10(), meal.getStrMeasure11(), meal.getStrMeasure12(),
                meal.getStrMeasure13(), meal.getStrMeasure14(), meal.getStrMeasure15(),
                meal.getStrMeasure16(), meal.getStrMeasure17(), meal.getStrMeasure18(),
                meal.getStrMeasure19(), meal.getStrMeasure20()
        };

        // Iterate through the arrays and add non-null values to the list
        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i] != null && !ingredients[i].isEmpty() && measures[i] != null && !measures[i].isEmpty()) {
                ingredientsList.add(new MealMeasureIngredient(meal.getIdMeal(), ingredients[i], measures[i]));
            }
        }

        return ingredientsList;
    }


}
