package com.example.mealmate.veiw.favorite_meals_fragment;

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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mealmate.veiw.favorite_meals_fragment.related_adapter_views.FavMealPagerAdapter;
import com.example.mealmate.presenter.favorite_meals_fragment_presenter.FavoriteMealsFragmentPresenter;
import com.example.mealmate.veiw.favorite_meals_fragment.favorite_meals_fragment_veiw_interface.FavoriteMealsFragmentVeiwInterface;
import com.example.mealmate.veiw.favorite_meals_fragment.favorite_meals_fragment_veiw_interface.HandelSeeMoreClick;
import com.example.mealmate.veiw.favorite_meals_fragment.favorite_meals_fragment_veiw_interface.Handel_Delete_Favorites;
import com.example.mealmate.R;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.example.mealmate.model.network.RemoteDataSourceImpl;
import com.example.mealmate.related_animation.ZoomOutPageTransformer;
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

    // Access UI elements in the custom layout
    TextView title ;
    TextView message;
    Button goButton ;
    Button cancelButton ;
    AlertDialog dialog;


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
        // Create an instance of AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        // Set the custom layout to the dialog
        builder.setView(dialogView);
        // Create and show the dialog
        dialog = builder.create();

        title = dialogView.findViewById(R.id.custom_title);
        message = dialogView.findViewById(R.id.custom_message);
        goButton = dialogView.findViewById(R.id.button_go);
        cancelButton = dialogView.findViewById(R.id.button_cancel);

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
        mealDTOS.clear();
        mealDTOS.addAll(data);
        favMealPagerAdapter.notifyDataSetChanged();
        Log.i(TAG, "showData: "+mealDTOS.size() +mealDTOS.get(0).getStrMeal()+mealDTOS.get(0).getIdMeal()
                +mealDTOS.get(0).getStrMealThumb()+mealDTOS.get(0).getStrArea()+mealDTOS.get(0).getStrCategory());

        Log.d(TAG, "showData: " + mealDTOS.size());
    }


    @Override
    public void showError() {

        title.setText(R.string.no_data_found);
        message.setText(R.string.add_your_meals_to_favorites_first);
        goButton.setText(R.string.ok);
        cancelButton.setText(R.string.back_to_home);

        goButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_favoriteMealsFragment_to_searchFragment);

        });
        cancelButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_favoriteMealsFragment_to_homeFragment);
        });
        dialog.show();

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

        title.setText(R.string.wait_are_you_sure);
        message.setText(R.string.delete_your_meals_from_favorites);
        goButton.setText(R.string.yes);
        cancelButton.setText(R.string.cancel);

        goButton.setOnClickListener(v -> {
            presenter.deleteFavoriteMeal(favoriteMeal);
            refreshFragment();
         });
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();

    }
    public void refreshFragment() {
        navController.navigate(R.id.favoriteMealsFragment);

    }



}