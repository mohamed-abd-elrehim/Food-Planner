package com.example.mealmate.veiw.home_fragment_veiw;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mealmate.presenter.home_fragment_presenter.HomeFragmentPresenterImpl;
import com.example.mealmate.veiw.home_fragment_veiw.related_adapter_views.MealCategoriesPagerAdapter;
import com.example.mealmate.veiw.home_fragment_veiw.related_adapter_views.MealIngredientesPagerAdapter;
import com.example.mealmate.veiw.home_fragment_veiw.related_adapter_views.MealOfTheDayPagerAdapter;
import com.example.mealmate.R;
import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealIngredient;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.network.network_Interface.RemoteDataSourceImpl;
import com.example.mealmate.related_animation.ZoomOutPageTransformer;
import com.example.mealmate.veiw.home_fragment_veiw.home_fragment_veiw_interface.HomeFragmentView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomeFragmentView {
    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;

    private MealCategoriesPagerAdapter mealCategoriesPagerAdapter;
    private MealIngredientesPagerAdapter mealIngredientesPagerAdapter;

    private MealOfTheDayPagerAdapter mealOfTheDayPagerAdapter;
    private HomeFragmentPresenterImpl presenter;
    private ArrayList<MealCategory> mealCategoriesDTOS;
    private ArrayList<MealIngredient> mealIngredientsDTOS;

    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        recyclerView = view.findViewById(R.id.viewPagerCategory);
        recyclerView2 =view.findViewById(R.id.viewPagerIngredient);

        presenter = new HomeFragmentPresenterImpl(AppDataBase.getInstance(getContext())
                , MealRepository.getInstance(
                LocalDataSourceImpl.getInstance(
                        AppDataBase.getInstance(getContext()).getFavoriteMealDAO(),
                        AppDataBase.getInstance(getContext()).getMealDAO(),
                        AppDataBase.getInstance(getContext()).getMealPlanDAO()
                ),
                RemoteDataSourceImpl.getInstance()),

                this);

        mealCategoriesDTOS = new ArrayList<>();
        mealCategoriesPagerAdapter = new MealCategoriesPagerAdapter(
                getContext(),
                mealCategoriesDTOS,
                mealCategory -> Toast.makeText(getContext(), mealCategory.getStrCategory(), Toast.LENGTH_SHORT).show()
        );

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(mealCategoriesPagerAdapter);

        mealIngredientsDTOS = new ArrayList<>();
        mealIngredientesPagerAdapter = new MealIngredientesPagerAdapter(
                getContext(),
                mealIngredientsDTOS,
                mealIngredient -> Toast.makeText(getContext(), mealIngredient.getStrIngredient(), Toast.LENGTH_SHORT).show()
        );
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView2.setAdapter(mealIngredientesPagerAdapter);


        // Load data
        presenter.loadMeals();
        presenter.loadMealsCategory();
        presenter.loadMealsIngredient();
    }

    @Override
    public void showData(List data) {
        if (data == null || data.isEmpty()) return;

        if (data.get(0) instanceof MealDTO) {
            List<MealDTO> meals = (List<MealDTO>) data;
            if (mealOfTheDayPagerAdapter == null) {
                mealOfTheDayPagerAdapter = new MealOfTheDayPagerAdapter(getContext(), meals);
                viewPager.setAdapter(mealOfTheDayPagerAdapter);
                viewPager.setPageTransformer(new ZoomOutPageTransformer());
            }
        } else if (data.get(0) instanceof MealCategory) {
            List<MealCategory> categories = (List<MealCategory>) data;
            mealCategoriesDTOS.addAll(categories);
            Log.i(TAG, "showData: ===="+mealCategoriesDTOS.size());
            mealCategoriesPagerAdapter.notifyDataSetChanged();
        }else if (data.get(0) instanceof MealIngredient) {
            List<MealIngredient> ingredients = (List<MealIngredient>) data;
            mealIngredientsDTOS.addAll(ingredients);
            Log.i(TAG, "showData: ===="+mealIngredientsDTOS.size());
            mealIngredientesPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
