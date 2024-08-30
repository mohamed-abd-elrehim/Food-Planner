
package com.example.mealmate.veiw.plan_of_the_week_fragment.plan_of_the_week_fragment_interface;

import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;

import java.util.ArrayList;
import java.util.List;

public interface PlanOfWeekFragmentVeiwInterface {
    void showData(List<MealDTO> data , List<MealPlan> mealPlans);
    void showError();
    void updateDayButtons();
    void setAvailableDays(List<String> days);
    void updateWeekRangeText(String weekRange);
    void setWeekRange(List<String> weekRanges);
    void setDay(List<String> days);



}
