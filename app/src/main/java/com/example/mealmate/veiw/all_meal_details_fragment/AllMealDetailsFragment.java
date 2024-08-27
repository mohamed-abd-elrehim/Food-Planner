package com.example.mealmate.veiw.all_meal_details_fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.veiw.all_meal_details_fragment.all_meal_details_fragment_veiw_interface.AllMealDetailsFragment_Veiw_Interface;
import com.example.mealmate.presenter.all_meal_details_fragment_presenter.AllMealDetailsFragment_presenter;
import com.example.mealmate.veiw.all_meal_details_fragment.all_meal_details_fragment_veiw_interface.HandelAddToFavoritesClick;
import com.example.mealmate.veiw.all_meal_details_fragment.all_meal_details_fragment_veiw_interface.HandelAddToPlanClick;
import com.example.mealmate.veiw.all_meal_details_fragment.related_adapter_views.IngredientAdapter;
import com.example.mealmate.veiw.all_meal_details_fragment.related_adapter_views.MediaPagerAdapter;
import com.example.mealmate.R;
import com.example.mealmate.veiw.all_meal_details_fragment.related_adapter_views.StepsAdapter;
import com.example.mealmate.model.MealIngredient;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.MediaItem;
import com.example.mealmate.model.Step;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;
import com.example.mealmate.model.network.RemoteDataSourceImpl;
import com.example.mealmate.veiw.main_activity.MainActivity;
import com.example.mealmate.veiw.search_fragment.search_fragment_veiw_interface.HandelSeeMoreClick;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class AllMealDetailsFragment extends Fragment implements AllMealDetailsFragment_Veiw_Interface, HandelAddToFavoritesClick , HandelAddToPlanClick
{

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

    private TextView dayNameTitle;
    private TextView dayName;
    private TextView mealTimeTitle;
    private TextView mealTime;


    private Button addToFavoritesButton;
    private Button backButton;
    private Button addPlanButton;

    CustomMeal customMeal = new CustomMeal();

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
        mealCategory = view.findViewById(R.id.all_Meal_detil_meal_categorie);
        mealArea = view.findViewById(R.id.all_Meal_detil_meal_area);
        backButton = view.findViewById(R.id.back);
        addToFavoritesButton = view.findViewById(R.id.add_to_Fav_button2);
        addPlanButton = view.findViewById(R.id.add_to_plan_button3);

        mealTime = view.findViewById(R.id.all_Meal_detil_meal_dateandtime);
        mealTimeTitle = view.findViewById(R.id.all_Meal_detil_meal_dateandtime1);
        dayName = view.findViewById(R.id.all_Meal_detil_meal_day);
        dayNameTitle = view.findViewById(R.id.all_Meal_detil_meal_day1);


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
        String arguments = AllMealDetailsFragmentArgs.fromBundle(getArguments()).getMeal();
        String fragment = AllMealDetailsFragmentArgs.fromBundle(getArguments()).getPage();
        MealPlan mealPlan = AllMealDetailsFragmentArgs.fromBundle(getArguments()).getMealPlan();
        // Check if arguments are not null and not empty
        if (arguments != null && !arguments.isEmpty()) {
            // Handle the case where the fragment is "searchFragment"
            if (fragment.equals("searchFragment")) {
                backButton.setVisibility(View.GONE);
                addToFavoritesButton.setVisibility(View.VISIBLE);
                addPlanButton.setVisibility(View.VISIBLE);
                presenter.loadAllMealDetailsById(arguments);

                mealTime.setVisibility(View.GONE);
                mealTimeTitle.setVisibility(View.GONE);
                dayName.setVisibility(View.GONE);
                dayNameTitle.setVisibility(View.GONE);

                // Handle the case where the fragment is "favoriteFragment"
            } else if (fragment.equals("favoriteFragment")) {
                backButton.setVisibility(View.VISIBLE);
                addToFavoritesButton.setVisibility(View.GONE);
                addPlanButton.setVisibility(View.VISIBLE);
                presenter.getFavMeals(arguments);
                mealTime.setVisibility(View.GONE);
                mealTimeTitle.setVisibility(View.GONE);
                dayName.setVisibility(View.GONE);
                dayNameTitle.setVisibility(View.GONE);

            } else if (fragment.equals("planOfTheWeekFragment")&&mealPlan!=null) {
                backButton.setVisibility(View.VISIBLE);
                addToFavoritesButton.setVisibility(View.VISIBLE);
                addPlanButton.setVisibility(View.GONE);
                presenter.getPlanMeals(arguments);

                mealTime.setVisibility(View.VISIBLE);
                mealTimeTitle.setVisibility(View.VISIBLE);
                dayName.setVisibility(View.VISIBLE);
                dayNameTitle.setVisibility(View.VISIBLE);

                mealTime.setText(mealPlan.getMealType());


                dayName.setText(mealPlan.getDayOfWeek());

            }
        } else {
            // Handle the case where arguments are null or empty
            // e.g., show an error message, default view, etc.
            Toast.makeText(getContext(), R.string.arguments_are_null_or_empty, Toast.LENGTH_SHORT).show();
        }


        addToFavoritesButton.setOnClickListener(view1 -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                onAddToFavoritesClick(customMeal);
            } else {
                showRestrictedAccessDialog();
            }


        });
        backButton.setOnClickListener(view1 -> {
            getActivity().onBackPressed();
        });
        addPlanButton.setOnClickListener(view1 -> {
            onAddToMealPlanClick(customMeal);
        });

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
        customMeal = data.get(0);
        mealMeasureIngredients.addAll(presenter.getMealMeasureIngredients(data.get(0)));

        mealName.setText(data.get(0).getStrMeal());
        mealCategory.setText(data.get(0).getStrCategory());
        mealArea.setText(data.get(0).getStrArea());

        Log.i(TAG, "showData:+============= " + mealMeasureIngredients.size());
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
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onAddToFavoritesClick(CustomMeal customMeal) {
        if (customMeal != null) {
            presenter.addMealToFAV(customMeal);
            Toast.makeText(getContext(), R.string.meal_added_to_favorites, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onAddToFavoritesClick: " + customMeal.getStrMeal());
        } else {
            Toast.makeText(getContext(), R.string.meal_is_null, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onAddToFavoritesClick: " + "meal is null");
        }
    }


    // Method to display the restricted access popup
    private void showRestrictedAccessDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Sign Up for More Features")
                .setMessage("Add your food preferences ,plan your meals and more!")
                .setPositiveButton("Sign Up", (dialog, which) -> {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("destination_fragment", "startFragment");
                    ;
                    startActivity(intent);
                    this.getActivity().finish();
                })
                .setNegativeButton("CANCEL", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void onAddToMealPlanClick(CustomMeal customMeal) {
        if (customMeal != null) {
            AllMealDetailsFragmentDirections.ActionAllMealDetailsFragmentToAddPlanMealFragment action = AllMealDetailsFragmentDirections.actionAllMealDetailsFragmentToAddPlanMealFragment(customMeal.idMeal,customMeal.strMealThumb);
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(action);
        }


    }
}