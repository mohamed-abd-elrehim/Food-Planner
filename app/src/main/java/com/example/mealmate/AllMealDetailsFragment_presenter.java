package com.example.mealmate;

import android.util.Log;

import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.Step;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealWithDetails;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMealWithMeals;
import com.example.mealmate.model.network.CustomMealResponse;
import com.example.mealmate.model.network.MealCategoryResponse;
import com.example.mealmate.model.network.network_Interface.NetworkCallback;
import com.example.mealmate.veiw.home_fragment_veiw.home_fragment_veiw_interface.HomeFragmentView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Response;

public class AllMealDetailsFragment_presenter implements AllMealDetailsFragment_presenter_Interface, NetworkCallback {
    private AllMealDetailsFragment_Veiw_Interface view;
    public static final String TAG = "AllMealDetailsFragment_presenter";
    MealRepository mealRepository;
    AppDataBase appDataBase;

    public AllMealDetailsFragment_presenter(AppDataBase appDataBase, MealRepository mealRepository, AllMealDetailsFragment_Veiw_Interface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.appDataBase = appDataBase;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }


    @Override
    public void loadAllMealDetailsById(String id) {
        mealRepository.makeNetworkCallback(this, "lookupAllMealDitailsById", id);

    }

    @Override
    public List<Step> processInstructions(String instructions) {
        if (instructions == null || instructions.trim().isEmpty()) {
            Log.e(TAG, "processInstructions: No instructions provided");
            return new ArrayList<>();
        }

        // Remove any \r or \n characters
        instructions = instructions.replace("\r", "").replace("\n", "");

        // Split the string into sentences based on periods
        String[] stepsArray = instructions.split("\\.");

        // Create an ArrayList to hold each step
        ArrayList<String> steps = new ArrayList<>();

        // Add each step to the ArrayList
        for (String step : stepsArray) {
            if (!step.trim().isEmpty()) {
                steps.add(step.trim() + ".");
            }
        }
        List<Step> stepsList=new ArrayList<>();

        for (int i = 0; i < steps.size(); i++) {
            stepsList.add(new Step("Step " + (i + 1), steps.get(i)));
        }


        return stepsList;

    }


    @Override
    public List<MealMeasureIngredient> getMealMeasureIngredients(CustomMeal meal) {
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

    @Override
    public void addMealToFAV(CustomMeal meal)
    {

        FavoriteMeal favoriteMeal=new FavoriteMeal(meal.getIdMeal(),FirebaseAuth.getInstance().getCurrentUser().getEmail(),meal.getStrMealThumb());
        List<MealMeasureIngredient> mealMeasureIngredient=getMealMeasureIngredients(meal);
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
        mealRepository.insertFavoriteMealWithMeals( favoriteMeal,mealDTO,mealMeasureIngredient);


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
}

