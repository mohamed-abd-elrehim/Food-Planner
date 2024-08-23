package com.example.mealmate;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.network.RemoteDataSourceImpl;
import com.example.mealmate.related_animation.ZoomOutPageTransformer;
import com.example.mealmate.veiw.favorite_meals_fragment.FavoriteMealsFragmentDirections;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class PlanOfTheWeekFragment extends Fragment implements PlanOfWeekFragmentVeiwInterface, PlanHandelSeeMoreClick, Handel_Delete_Plans {

    private ViewPager2 viewPager;
    private PlanMealPagerAdapter planMealPagerAdapter;
    private PlanOfTheWeekFragmentPresenter presenter;
    private ArrayList<MealDTO> mealDTOS = new ArrayList<>();
    private ArrayList<MealPlan> mealPlans = new ArrayList<>();
    private static final String TAG = "PlanOfTheWeekFragment";

    private NavController navController;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan_of_the_week, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.favorite_Meal_ViewPager);
        progressBar = view.findViewById(R.id.progressBar);
        navController = Navigation.findNavController(view);
        presenter = new PlanOfTheWeekFragmentPresenter(AppDataBase.getInstance(getContext())
                , MealRepository.getInstance(
                LocalDataSourceImpl.getInstance(
                        AppDataBase.getInstance(getContext()).getFavoriteMealDAO(),
                        AppDataBase.getInstance(getContext()).getMealDAO(),
                        AppDataBase.getInstance(getContext()).getMealPlanDAO()
                ),
                RemoteDataSourceImpl.getInstance()),

                this
        );

        mealDTOS = new ArrayList<>();
        mealPlans = new ArrayList<>();

        // Initialize the adapter
        planMealPagerAdapter = new PlanMealPagerAdapter(getContext(), mealDTOS, mealPlans,this, this);


        viewPager.setAdapter(planMealPagerAdapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        presenter.getAllPlanOfWeeksMeals(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    @Override
    public void showData(List<MealDTO> data, List<MealPlan> planMeals) {
        // Initialize the ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (data == null || data.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                showError();
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }, 5000);

        mealDTOS.clear();
        mealPlans.clear();
        mealPlans.addAll(planMeals);
        mealDTOS.addAll(data);

        planMealPagerAdapter.notifyDataSetChanged();
        Log.i(TAG, "showData: " + mealDTOS.size() + mealDTOS.get(0).getStrMeal() + mealDTOS.get(0).getIdMeal()
                + mealDTOS.get(0).getStrMealThumb() + mealDTOS.get(0).getStrArea() + mealDTOS.get(0).getStrCategory());

        Log.d(TAG, "showData: " + mealDTOS.size());
    }

    @Override
    public void onSeeMoreClick(MealDTO meal) {
        String id = meal.getIdMeal();
        if (id != null) {
            PlanOfTheWeekFragmentDirections.ActionPlanOfTheWeekFragmentToAllMealDetailsFragment action =
                    PlanOfTheWeekFragmentDirections.actionPlanOfTheWeekFragmentToAllMealDetailsFragment(id,"planOfTheWeekFragment");
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(action);

        }


    }

    @Override
    public void onDeletePlansClick(MealPlan mealPlan) {
        new AlertDialog.Builder(getContext())
                .setTitle("Wait!Are You Sure")
                .setMessage("Delete your meals from plans.")
                .setPositiveButton("YES", (dialog, which) -> {
                    presenter.deletePlanMeal(mealPlan);
                })
                .setNegativeButton("CANCEL", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

    }


    @Override
    public void showError() {
        new AlertDialog.Builder(getContext())
                .setTitle("No Data Found")
                .setMessage("Add your meals to Plan first.")
                .setPositiveButton("OK", (dialog, which) -> {
                    navController.navigate(R.id.action_favoriteMealsFragment_to_searchFragment);

                })
                .setNegativeButton("Back To Home", (dialog, which) -> {
                    navController.navigate(R.id.action_favoriteMealsFragment_to_homeFragment);
                })
                .show();

    }
}