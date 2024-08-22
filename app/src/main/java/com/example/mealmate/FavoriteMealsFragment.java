package com.example.mealmate;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.network.RemoteDataSourceImpl;
import com.example.mealmate.presenter.home_fragment_presenter.HomeFragmentPresenterImpl;
import com.example.mealmate.related_animation.ZoomOutPageTransformer;
import com.example.mealmate.veiw.home_activity.HomeActivity;
import com.example.mealmate.veiw.home_fragment_veiw.related_adapter_views.MealOfTheDayPagerAdapter;
import com.example.mealmate.veiw.search_fragment.SearchFragmentDirections;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FavoriteMealsFragment extends Fragment implements FavoriteMealsFragmentVeiwInterface, HandelSeeMoreClick, Handel_Delete_Favorites {
    private ViewPager2 viewPager;
    private FavMealPagerAdapter favMealPagerAdapter;
    private FavoriteMealsFragmentPresenter presenter;
    private ArrayList<MealDTO> mealDTOS = new ArrayList<>();
    private static final String TAG = "FavoriteMealsFragment";
    private NavController navController;
    ProgressBar progressBar ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_meals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.favorite_Meal_ViewPager);
        progressBar = view.findViewById(R.id.progressBar);
        navController = Navigation.findNavController(view);
        presenter = new FavoriteMealsFragmentPresenter(AppDataBase.getInstance(getContext())
                , MealRepository.getInstance(
                LocalDataSourceImpl.getInstance(
                        AppDataBase.getInstance(getContext()).getFavoriteMealDAO(),
                        AppDataBase.getInstance(getContext()).getMealDAO(),
                        AppDataBase.getInstance(getContext()).getMealPlanDAO()
                ),
                RemoteDataSourceImpl.getInstance()),

                this);

        mealDTOS = new ArrayList<>();
        // Initialize the adapter
        favMealPagerAdapter = new FavMealPagerAdapter(getContext(), mealDTOS, this, this);
        viewPager.setAdapter(favMealPagerAdapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        presenter.getAllFAVMeals(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }


    @Override
    public void showData(List<MealDTO> data) {
        // Initialize the ProgressBar

// Show the ProgressBar
        progressBar.setVisibility(View.VISIBLE);

// Delay execution by 5 seconds (5000 milliseconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check if data is null or empty
            if (data == null || data.isEmpty()) {
                // Stop the loading indicator
                progressBar.setVisibility(View.GONE);

                // Show the error message after 5 seconds delay
                showError();
            } else {
                // Stop the loading indicator if data is valid
                progressBar.setVisibility(View.GONE);

            }
        }, 5000); // 5-second delay to simulate loading


        mealDTOS.clear();
        mealDTOS.addAll(data);
        favMealPagerAdapter.notifyDataSetChanged();
        Log.i(TAG, "showData: "+mealDTOS.size() +mealDTOS.get(0).getStrMeal()+mealDTOS.get(0).getIdMeal()
                +mealDTOS.get(0).getStrMealThumb()+mealDTOS.get(0).getStrArea()+mealDTOS.get(0).getStrCategory());

        Log.d(TAG, "showData: " + mealDTOS.size());
    }


    @Override
    public void showError() {
        new AlertDialog.Builder(getContext())
                .setTitle("No Data Found")
                .setMessage("Add your meals to favorites first.")
                .setPositiveButton("OK", (dialog, which) -> {
                    navController.navigate(R.id.action_favoriteMealsFragment_to_searchFragment);

                })
                .setNegativeButton("Back To Home", (dialog, which) -> {
                    navController.navigate(R.id.action_favoriteMealsFragment_to_homeFragment);
                })
                .show();

    }

    @Override
    public void onSeeMoreClick(MealDTO meal) {
        String id = meal.getIdMeal();
        if (id != null) {
            FavoriteMealsFragmentDirections.ActionFavoriteMealsFragmentToAllMealDetailsFragment action = FavoriteMealsFragmentDirections.actionFavoriteMealsFragmentToAllMealDetailsFragment(id,"favoriteFragment");
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(action);

        }

    }

    @Override
    public void onDeleteFavoritesClick(FavoriteMeal favoriteMeal) {
        new AlertDialog.Builder(getContext())
                .setTitle("Wait!Are You Sure")
                .setMessage("Delete your meals from favorites.")
                .setPositiveButton("YES", (dialog, which) -> {
                    presenter.deleteFavoriteMeal(favoriteMeal);
                })
        .setNegativeButton("CANCEL", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

    }

}