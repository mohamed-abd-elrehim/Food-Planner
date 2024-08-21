package com.example.mealmate;

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
import android.widget.TextView;

import com.example.mealmate.model.MealIngredient;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.MediaItem;
import com.example.mealmate.model.Step;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.network.CustomMealResponse;
import com.example.mealmate.model.network.RemoteDataSourceImpl;
import com.example.mealmate.presenter.search_fragment_presenter.Search_Fragment_PresenterImpl;

import java.util.ArrayList;
import java.util.List;


public class AllMealDetailsFragment extends Fragment implements AllMealDetailsFragment_Veiw_Interface {

    private static final String TAG = "AllMealDetailsFragment";
    private ArrayList<MealIngredient> mealIngredients = new ArrayList<>();
    private ArrayList<CustomMeal> customMeals = new ArrayList<>();

    private RecyclerView stepsRecyclerView;
    private StepsAdapter stepsAdapter;
    private List<Step> stepsList;
    private ArrayList<String> stepsListHandeled = new ArrayList<>();
    private MediaPagerAdapter mediaPagerAdapter;

    private ViewPager2 all_Meal_detil_ViewPager;
    private List<MediaItem> mediaItems = new ArrayList<>();

    private List<MealMeasureIngredient> mealMeasureIngredients = new ArrayList<>();
    private IngredientAdapter ingredientAdapter;
    private RecyclerView ingredientRecyclerView;

    private TextView mealName;
    private TextView mealCategory;
    private TextView mealArea;

    private AllMealDetailsFragment_presenter presenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_meal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stepsRecyclerView = view.findViewById(R.id.all_Meal_detil_mealDirections_recyclerView3);
        all_Meal_detil_ViewPager = view.findViewById(R.id.all_Meal_detil_ViewPager);
        ingredientRecyclerView = view.findViewById(R.id.all_Meal_detil_mealIngredients_recyclerView2);
        mealName = view.findViewById(R.id.all_Meal_detil_meal_name);
        mealCategory= view.findViewById(R.id.all_Meal_detil_meal_categorie);
        mealArea= view.findViewById(R.id.all_Meal_detil_meal_area);
        // Set up RecyclerView
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Initialize steps list and adapter
        stepsList = new ArrayList<>();
        stepsAdapter = new StepsAdapter(getContext(), stepsList);
        stepsRecyclerView.setAdapter(stepsAdapter);

        // Initialize ViewPager
        mediaItems = new ArrayList<>();
        mediaPagerAdapter = new MediaPagerAdapter(getContext(), mediaItems);
        all_Meal_detil_ViewPager.setAdapter(mediaPagerAdapter);

        mealMeasureIngredients = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(getContext(), mealMeasureIngredients);
        ingredientRecyclerView.setAdapter(ingredientAdapter);


        // Initialize the presenter
        presenter = new AllMealDetailsFragment_presenter(
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

        // Load meal details by ID
        String mealID = AllMealDetailsFragmentArgs.fromBundle(getArguments()).getMealID();
        Log.i(TAG, "onViewCreated: Meal ID - " + mealID);
        presenter.loadAllMealDetailsById(mealID);
    }


    @Override
    public void showData(List<CustomMeal> data) {
        if (data == null || data.isEmpty()) {
            Log.e(TAG, "showData: No meal data received");
            return;
        }
    // Clear any existing data
        stepsList.clear();
        mediaItems.clear();

        mealMeasureIngredients.clear();

        mealMeasureIngredients .addAll(presenter.getMealMeasureIngredients(data.get(0)));

        mealName.setText(data.get(0).getStrMeal());
        mealCategory.setText(data.get(0).getStrCategory());
        mealArea.setText(data.get(0).getStrArea());

        Log.i(TAG, "showData:+============= "+mealMeasureIngredients.size());
        String imagUrl = data.get(0).getStrMealThumb();
        String videoUrl = data.get(0).getStrYoutube();
        mediaItems.add(new MediaItem(imagUrl, false));
        mediaItems.add(new MediaItem(videoUrl, true));
        // Get the instructions from the first CustomMeal
        String instructions = data.get(0).getStrInstructions();

        // Process instructions into steps
        // Populate the stepsList with Step objects
        stepsList.addAll(presenter.processInstructions(instructions));




        // Notify the adapter that the data has changed
        stepsAdapter.notifyDataSetChanged();
        mediaPagerAdapter.notifyDataSetChanged();
        ingredientAdapter.notifyDataSetChanged();


    }


    @Override
    public void showError(String errorMessage) {

    }





}