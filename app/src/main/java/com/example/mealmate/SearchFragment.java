package com.example.mealmate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealIngredient;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.network.network_Interface.RemoteDataSourceImpl;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements Search_Fragment_Veiw_Interface {

    private Chip filterArea;
    private Chip filterCategory;
    private Chip filterIngredient;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private AllMealPagerAdapter allMealPagerAdapter;
    private FilterAdapter<MealCategory> filterAdapterCategory;
    private FilterAdapter<MealIngredient> filterAdapterIngredient;
    private ArrayList<MealDTO> mealsList = new ArrayList<>();
    private ArrayList<MealCategory> categoryFilterList = new ArrayList<>();
    private ArrayList<MealIngredient> ingredientFilterList = new ArrayList<>();
    private Search_Fragment_PresenterImpl presenter;
private static final String TAG = "SearchFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filterArea = view.findViewById(R.id.area_chip);
        filterCategory = view.findViewById(R.id.categories_chip2);
        filterIngredient = view.findViewById(R.id.ingredients_chip);
        recyclerView = view.findViewById(R.id.search_recyclerview);
        recyclerView2 = view.findViewById(R.id.recyclerView);

        // Initialize the presenter
        presenter = new Search_Fragment_PresenterImpl(
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

        // Setup meal list RecyclerView
        allMealPagerAdapter = new AllMealPagerAdapter(
                getContext(),
                mealsList,
                mealDTO -> Toast.makeText(getContext(), mealDTO.getStrMeal(), Toast.LENGTH_SHORT).show()
        );
        setupRecyclerView(recyclerView, allMealPagerAdapter);

        // Setup filter RecyclerViews
        filterAdapterCategory = createFilterAdapter(categoryFilterList, MealCategory.class);
        filterAdapterIngredient = createFilterAdapter(ingredientFilterList, MealIngredient.class);



        // Set up filter button actions
        filterCategory.setOnClickListener(v ->{
            presenter.loadAllCategoriess();
            setupRecyclerView(recyclerView2, filterAdapterCategory);
            Log.i(TAG, "filterCategory: ");
        } );
        Log.i(TAG, "onViewCreated: ");
        filterIngredient.setOnClickListener(v -> {
            presenter.loadAllIngredient();
            setupRecyclerView(recyclerView2, filterAdapterIngredient);
            Log.i(TAG, "filterIngredient: ");
        });
        // Add other filter actions as needed
    }

    private <T> FilterAdapter<T> createFilterAdapter(List<T> filterList, Class<T> type) {
        return new FilterAdapter<>(
                getContext(),
                filterList,
                meal -> {
                    if (type.equals(MealCategory.class)) {
                        presenter.loadFilteredCategoriess(((MealCategory) meal).getStrCategory());
                        Log.i(TAG, "createFilterAdapter: "+ ((MealCategory) meal).getStrCategory());
                        Toast.makeText(getContext(), ((MealCategory) meal).getStrCategory(), Toast.LENGTH_SHORT).show();
                    } else if (type.equals(MealIngredient.class)) {
                        Toast.makeText(getContext(), ((MealIngredient) meal).getStrIngredient(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        Log.i(TAG, "setupRecyclerView: "+recyclerView.getAdapter());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showData(List data) {
        if (data == null || data.isEmpty()) return;

        if (data.get(0) instanceof MealCategory) {
            Log.i(TAG, "showData: "+((List<MealCategory>) data).size());
            filterAdapterCategory.updateFilterList((List<MealCategory>) data);
        } else if (data.get(0) instanceof MealIngredient) {
            filterAdapterIngredient.updateFilterList((List<MealIngredient>) data);
        } else if (data.get(0) instanceof MealDTO) {
            mealsList.clear();
            mealsList.addAll((List<MealDTO>) data);
            allMealPagerAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
