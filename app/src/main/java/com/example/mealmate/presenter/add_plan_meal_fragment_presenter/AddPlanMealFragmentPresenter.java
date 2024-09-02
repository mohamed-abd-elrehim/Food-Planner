package com.example.mealmate.presenter.add_plan_meal_fragment_presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.mealmate.presenter.add_plan_meal_fragment_presenter.add_plan_meal_fragment_presenter_interface.AddPlanMealFragmentPresenterInterFace;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.network.CustomMealResponse;
import com.example.mealmate.model.network.network_Interface.NetworkCallback;
import com.example.mealmate.veiw.add_plan_meal_fragment.add_plan_meal_fragment_veiw_interface.AddPlanMealFragmentVeiwInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Response;

public class AddPlanMealFragmentPresenter implements AddPlanMealFragmentPresenterInterFace, NetworkCallback {

    private AddPlanMealFragmentVeiwInterface view;
    public static final String TAG = "AllMealDetailsFragment_presenter";
    MealRepository mealRepository;


    private LiveData<MealDTO> mealDTO;
    private LiveData<List<MealMeasureIngredient>> mealMeasureIngredient;
    private LiveData<List<CustomMeal>> customMeals;

    public AddPlanMealFragmentPresenter( MealRepository mealRepository, AddPlanMealFragmentVeiwInterface view) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.mealRepository.updateBaseUrl("https://www.themealdb.com/api/json/v1/1/");
    }



    @Override
    public void addMealToPaln(MealPlan mealPlan, CustomMeal meal) {
        List<MealMeasureIngredient> mealMeasureIngredient = getMealMeasureIngredients(meal);
        MealDTO mealDTO = new MealDTO(
                "mealplan",
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
                ingredientsList.add(new MealMeasureIngredient("mealplan",meal.getIdMeal(), ingredients[i], measures[i]));
            }
        }

        return ingredientsList;
    }

    public void updateWeekRange(Calendar currentWeek, SimpleDateFormat dateFormat) {
        // Set Cairo time zone
        TimeZone cairoTimeZone = TimeZone.getTimeZone("Africa/Cairo");

        // Set time zone for the current week calendar
        currentWeek.setTimeZone(cairoTimeZone);

        // Start of the week
        Calendar startOfWeek = (Calendar) currentWeek.clone();
        startOfWeek.setTimeZone(cairoTimeZone);
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // End of the week
        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.setTimeZone(cairoTimeZone);
        endOfWeek.add(Calendar.DAY_OF_WEEK, 6);

        // Set the Cairo time zone for the date format
        dateFormat.setTimeZone(cairoTimeZone);

        // Create the week range string
        String weekRange = dateFormat.format(startOfWeek.getTime()) + " - " + dateFormat.format(endOfWeek.getTime());
        view.updateWeekRangeText(weekRange);

        // List to store available days from the current day to Sunday
        List<String> availableDays = new ArrayList<>();

        // Date format for the day name (e.g., Monday, Tuesday)
        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        dayNameFormat.setTimeZone(cairoTimeZone);

        // Get the current day in the week
        Calendar currentDay = Calendar.getInstance();
        currentDay.setTimeZone(cairoTimeZone);
        currentDay.set(Calendar.HOUR_OF_DAY, 0);
        currentDay.set(Calendar.MINUTE, 0);
        currentDay.set(Calendar.SECOND, 0);
        currentDay.set(Calendar.MILLISECOND, 0);

        // If the current day is before the start of the week, set it to start of the week
        if (currentDay.before(startOfWeek)) {
            currentDay = (Calendar) startOfWeek.clone();
        }

        // Iterate from the current day to the end of the week
        while (!currentDay.after(endOfWeek)) {
            String dayName = dayNameFormat.format(currentDay.getTime());
            Log.i(TAG, "updateWeekRange: " + dayName);
            availableDays.add(dayName);
            currentDay.add(Calendar.DAY_OF_MONTH, 1);
        }

        Log.i(TAG, "updateWeekRange: " + availableDays.toString());

        view.setAvailableDays(availableDays);
        view.updateDayButtons();

    }

}
