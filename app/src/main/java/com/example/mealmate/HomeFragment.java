package com.example.mealmate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.network.network_Interface.RemoteDataSourceImpl;
import com.example.mealmate.veiw.ZoomOutPageTransformer;

import java.util.List;

public class HomeFragment extends Fragment implements HomeFragmentView {
    private ViewPager2 viewPager;
    private MealPagerAdapter mealPagerAdapter;
    private HomeFragmentPresenterImpl presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = view.findViewById(R.id.viewPager);

        presenter = new HomeFragmentPresenterImpl(
                AppDataBase.getInstance(getContext()),
                MealRepository.getInstance(
                        LocalDataSourceImpl.getInstance(
                                AppDataBase.getInstance(getContext()).getFavoriteMealDAO(),
                                AppDataBase.getInstance(getContext()).getMealDAO(),
                                AppDataBase.getInstance(getContext()).getMealPlanDAO()
                        ),
                        RemoteDataSourceImpl.getInstance()
                ),
                this
        );

        presenter.loadMeals();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void showMeals(List<MealDTO> meals) {
        mealPagerAdapter = new MealPagerAdapter(getContext(), meals);
        viewPager.setAdapter(mealPagerAdapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
    }



    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}