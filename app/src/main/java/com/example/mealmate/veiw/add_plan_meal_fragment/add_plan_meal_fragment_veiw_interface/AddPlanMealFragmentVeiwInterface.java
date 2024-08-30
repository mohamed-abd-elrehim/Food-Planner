package com.example.mealmate.veiw.add_plan_meal_fragment.add_plan_meal_fragment_veiw_interface;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import java.util.List;

public interface AddPlanMealFragmentVeiwInterface {
    void showData(List<CustomMeal> data);
    void showError(String errorMessage);
    void updateDayButtons();
    void updateWeekRangeText(String weekRange);
    void setAvailableDays(List<String> days);

}
