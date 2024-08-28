package com.example.mealmate.presenter.all_meal_details_fragment_presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.mealmate.presenter.all_meal_details_fragment_presenter.all_meal_details_fragment_presenter_interface.AllMealDetailsFragment_presenter_Interface;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.Step;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.network.CustomMealResponse;
import com.example.mealmate.model.network.network_Interface.NetworkCallback;
import com.example.mealmate.veiw.all_meal_details_fragment.all_meal_details_fragment_veiw_interface.AllMealDetailsFragment_Veiw_Interface;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class AllMealDetailsFragment_presenter implements AllMealDetailsFragment_presenter_Interface, NetworkCallback {
    private AllMealDetailsFragment_Veiw_Interface view;
    public static final String TAG = "AllMealDetailsFragment_presenter";
    MealRepository mealRepository;

    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredient;
    private LiveData<List<CustomMeal>> customMeals;

    public AllMealDetailsFragment_presenter(MealRepository mealRepository, AllMealDetailsFragment_Veiw_Interface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }


    @Override
    public void loadAllMealDetailsById(String id) {
        mealRepository.makeNetworkCallback(this, "lookupAllMealDitailsById", id);

    }


    @Override
    public void getFavMeals(String id) {
        mealDTO = mealRepository.getMealById(id);
        mealDTO.observeForever(meals -> {
            if (meals != null) {
                LiveData<List<MealMeasureIngredient>> mealMeasureIngredient = mealRepository.getIngredientsByMealId(id);
                mealMeasureIngredient.observeForever(mealMeasureIngredients -> {
                    if (mealMeasureIngredients != null && !mealMeasureIngredients.isEmpty()) {
                        List<CustomMeal> customMeals = new ArrayList<>();
                        customMeals.add(convertToCustomMeal(meals, mealMeasureIngredients));
                        Log.i(TAG, "getFavMeals: "+ customMeals.size()+" Meals found.");
                        view.showData(customMeals);
                    }
                });
            }


        });


    }

    @Override
    public void getPlanMeals(String id) {
        mealDTO = mealRepository.getMealById(id);
        mealDTO.observeForever(meals -> {
            if (meals != null) {
                LiveData<List<MealMeasureIngredient>> mealMeasureIngredient = mealRepository.getIngredientsByMealId(id);
                mealMeasureIngredient.observeForever(mealMeasureIngredients -> {
                    if (mealMeasureIngredients != null && !mealMeasureIngredients.isEmpty()) {
                        List<CustomMeal> customMeals = new ArrayList<>();
                        customMeals.add(convertToCustomMeal(meals, mealMeasureIngredients));
                        Log.i(TAG, "getFavMeals: "+ customMeals.size()+" Meals found.");
                        view.showData(customMeals);
                    }
                });
            }


        });


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
        List<Step> stepsList = new ArrayList<>();

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
                ingredientsList.add(new MealMeasureIngredient("mealfavorite",meal.getIdMeal(), ingredients[i], measures[i]));
            }
        }

        return ingredientsList;
    }

    @Override
    public void addMealToFAV(CustomMeal meal) {

        FavoriteMeal favoriteMeal = new FavoriteMeal(meal.getIdMeal(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), meal.getStrMealThumb());
        List<MealMeasureIngredient> mealMeasureIngredient = getMealMeasureIngredients(meal);
        MealDTO mealDTO = new MealDTO(
                "mealfavorite",
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
        mealRepository.insertFavoriteMealWithMeals(favoriteMeal, mealDTO, mealMeasureIngredient);


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


    public CustomMeal convertToCustomMeal(MealDTO mealDTO, List<MealMeasureIngredient> ingredients) {
        CustomMeal customMeal = new CustomMeal();

        // Set mealDTO properties
        customMeal.setIdMeal(mealDTO.getIdMeal());
        customMeal.setStrMeal(mealDTO.getStrMeal());
        customMeal.setStrDrinkAlternate(mealDTO.getStrDrinkAlternate());
        customMeal.setStrCategory(mealDTO.getStrCategory());
        customMeal.setStrArea(mealDTO.getStrArea());
        customMeal.setStrInstructions(mealDTO.getStrInstructions());
        customMeal.setStrMealThumb(mealDTO.getStrMealThumb());
        customMeal.setStrTags(mealDTO.getStrTags());
        customMeal.setStrYoutube(mealDTO.getStrYoutube());
        customMeal.setStrSource(mealDTO.getStrSource());
        customMeal.setStrImageSource(mealDTO.getStrImageSource());
        customMeal.setStrCreativeCommonsConfirmed(mealDTO.getStrCreativeCommonsConfirmed());
        customMeal.setDateModified(mealDTO.getDateModified());

        // Initialize ingredient and measure arrays
        String[] ingredientsArray = new String[20];
        String[] measuresArray = new String[20];

        // Populate ingredient and measure arrays
        for (int i = 0; i < ingredients.size(); i++) {
            MealMeasureIngredient ingredient = ingredients.get(i);
            if (i < 20) {
                ingredientsArray[i] = ingredient.getIngredientName();
                measuresArray[i] = ingredient.getMeasure();
            }
        }

        // Set ingredient and measure properties
        customMeal.setStrIngredient1(getValue(ingredientsArray, 0));
        customMeal.setStrIngredient2(getValue(ingredientsArray, 1));
        customMeal.setStrIngredient3(getValue(ingredientsArray, 2));
        customMeal.setStrIngredient4(getValue(ingredientsArray, 3));
        customMeal.setStrIngredient5(getValue(ingredientsArray, 4));
        customMeal.setStrIngredient6(getValue(ingredientsArray, 5));
        customMeal.setStrIngredient7(getValue(ingredientsArray, 6));
        customMeal.setStrIngredient8(getValue(ingredientsArray, 7));
        customMeal.setStrIngredient9(getValue(ingredientsArray, 8));
        customMeal.setStrIngredient10(getValue(ingredientsArray, 9));
        customMeal.setStrIngredient11(getValue(ingredientsArray, 10));
        customMeal.setStrIngredient12(getValue(ingredientsArray, 11));
        customMeal.setStrIngredient13(getValue(ingredientsArray, 12));
        customMeal.setStrIngredient14(getValue(ingredientsArray, 13));
        customMeal.setStrIngredient15(getValue(ingredientsArray, 14));
        customMeal.setStrIngredient16(getValue(ingredientsArray, 15));
        customMeal.setStrIngredient17(getValue(ingredientsArray, 16));
        customMeal.setStrIngredient18(getValue(ingredientsArray, 17));
        customMeal.setStrIngredient19(getValue(ingredientsArray, 18));
        customMeal.setStrIngredient20(getValue(ingredientsArray, 19));

        customMeal.setStrMeasure1(getValue(measuresArray, 0));
        customMeal.setStrMeasure2(getValue(measuresArray, 1));
        customMeal.setStrMeasure3(getValue(measuresArray, 2));
        customMeal.setStrMeasure4(getValue(measuresArray, 3));
        customMeal.setStrMeasure5(getValue(measuresArray, 4));
        customMeal.setStrMeasure6(getValue(measuresArray, 5));
        customMeal.setStrMeasure7(getValue(measuresArray, 6));
        customMeal.setStrMeasure8(getValue(measuresArray, 7));
        customMeal.setStrMeasure9(getValue(measuresArray, 8));
        customMeal.setStrMeasure10(getValue(measuresArray, 9));
        customMeal.setStrMeasure11(getValue(measuresArray, 10));
        customMeal.setStrMeasure12(getValue(measuresArray, 11));
        customMeal.setStrMeasure13(getValue(measuresArray, 12));
        customMeal.setStrMeasure14(getValue(measuresArray, 13));
        customMeal.setStrMeasure15(getValue(measuresArray, 14));
        customMeal.setStrMeasure16(getValue(measuresArray, 15));
        customMeal.setStrMeasure17(getValue(measuresArray, 16));
        customMeal.setStrMeasure18(getValue(measuresArray, 17));
        customMeal.setStrMeasure19(getValue(measuresArray, 18));
        customMeal.setStrMeasure20(getValue(measuresArray, 19));

        return customMeal;
    }

    private static String getValue(String[] array, int index) {
        return (index < array.length) ? array[index] : null;
    }
}

