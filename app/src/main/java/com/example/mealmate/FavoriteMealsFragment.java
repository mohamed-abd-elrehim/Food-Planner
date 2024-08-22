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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.network.RemoteDataSourceImpl;
import com.example.mealmate.presenter.home_fragment_presenter.HomeFragmentPresenterImpl;
import com.example.mealmate.related_animation.ZoomOutPageTransformer;
import com.example.mealmate.veiw.home_activity.HomeActivity;
import com.example.mealmate.veiw.home_fragment_veiw.related_adapter_views.MealOfTheDayPagerAdapter;
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
        if (data == null || data.isEmpty()) {
            showError();
            return;
        }

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

    }

    @Override
    public void onDeleteFavoritesClick(MealDTO meal) {


    }

}