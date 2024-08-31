package com.example.mealmate.veiw.search_fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.mealmate.veiw.main_activity.MainActivity;
import com.example.mealmate.veiw.search_fragment.related_adapter_views.AllMealPagerAdapter;
import com.example.mealmate.veiw.search_fragment.related_adapter_views.FilterAdapter;
import com.example.mealmate.R;
import com.example.mealmate.presenter.search_fragment_presenter.Search_Fragment_PresenterImpl;
import com.example.mealmate.veiw.search_fragment.search_fragment_veiw_interface.HandelSeeMoreClick;
import com.example.mealmate.veiw.search_fragment.search_fragment_veiw_interface.Search_Fragment_Veiw_Interface;
import com.example.mealmate.veiw.search_fragment.related_adapter_views.SuggestionsAdapter;
import com.example.mealmate.model.MealArea;
import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealIngredient;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.network.RemoteDataSourceImpl;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements Search_Fragment_Veiw_Interface, HandelSeeMoreClick {

    private Chip filterArea;
    private Chip filterCategory;
    private Chip filterIngredient;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private AllMealPagerAdapter allMealPagerAdapter;
    private FilterAdapter<MealCategory> filterAdapterCategory;
    private FilterAdapter<MealIngredient> filterAdapterIngredient;
    private FilterAdapter<MealArea> mealAreaFilterAdapter;
    private ArrayList<MealDTO> mealsList = new ArrayList<>();
    private ArrayList<MealDTO> mealsSearchList = new ArrayList<>();
    private ArrayList<MealCategory> categoryFilterList = new ArrayList<>();
    private ArrayList<MealIngredient> ingredientFilterList = new ArrayList<>();
    private ArrayList<MealArea> areaFilterList = new ArrayList<>();
    private SearchView searchView;
    private RecyclerView suggestionsRecyclerView;
    private SuggestionsAdapter<Object> suggestionsAdapter;
    private TextView fallbackMessage;
    private Search_Fragment_PresenterImpl presenter;
    private static final String TAG = "SearchFragment";
    private String filterName;
    private String filterType;

    String mode = null;
    // Access UI elements in the custom layout
    TextView title;
    TextView message;
    Button goButton;
    Button cancelButton;
    AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the string passed through the bundle
        if (getArguments() != null) {
            mode = getArguments().getString("user_type");
        }

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
        searchView = view.findViewById(R.id.searchView);
        suggestionsRecyclerView = view.findViewById(R.id.suggestionsRecyclerView);
        fallbackMessage = view.findViewById(R.id.fallbackMessage);
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


        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        suggestionsAdapter = new SuggestionsAdapter<>(getContext(), new ArrayList<>(), item -> {
            if (item instanceof MealDTO) {
                if (mode!=null&&mode.equals("guest")) {
                    showRestrictedAccessDialog();
                }else
                {
                    SearchFragmentDirections.ActionSearchFragmentToAllMealDetailsFragment action = SearchFragmentDirections.actionSearchFragmentToAllMealDetailsFragment(((MealDTO) item).getIdMeal(), "searchFragment");
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(action);
                }

            } else if (item instanceof MealCategory) {
                presenter.loadFilteredCategoriess(((MealCategory) item).getStrCategory());
                searchView.clearFocus();
                searchView.setQuery("", false);


            } else if (item instanceof MealIngredient) {
                presenter.loadFilteredIngredient(((MealIngredient) item).getStrIngredient());
                searchView.clearFocus();
                searchView.setQuery("", false);
            }else if (item instanceof MealArea) {
                presenter.loadFilteredArea(((MealArea) item).getStrArea());
                searchView.clearFocus();
                searchView.setQuery("", false);
            }

            // Handle item click
        });
        suggestionsRecyclerView.setAdapter(suggestionsAdapter);

        if (searchView == null) {
            Log.e(TAG, "SearchView is null");
            return;
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle query submission
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle empty query
                if (newText == null || newText.isEmpty()) {
                    suggestionsRecyclerView.setVisibility(View.GONE);
                    fallbackMessage.setVisibility(View.GONE);
                    return true;
                }

                updateSuggestions(newText);
                return true;
            }
        });

        // Initialize the presenter
        presenter = new Search_Fragment_PresenterImpl(
                MealRepository.getInstance(
                        LocalDataSourceImpl.getInstance(
                                AppDataBase.getInstance(getContext()).getFavoriteMealDAO(),
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
                mealDTO -> Toast.makeText(getContext(), mealDTO.getStrMeal(), Toast.LENGTH_SHORT).show(),
                this
        );
        setupRecyclerView(recyclerView, allMealPagerAdapter);

        // Setup filter RecyclerViews
        filterAdapterCategory = createFilterAdapter(categoryFilterList, MealCategory.class);
        filterAdapterIngredient = createFilterAdapter(ingredientFilterList, MealIngredient.class);
        mealAreaFilterAdapter = createFilterAdapter(areaFilterList, MealArea.class);

        presenter.loadAllCategoriess();
        presenter.loadAllIngredient();
        presenter.loadAllArea();

        SearchFragmentArgs args = SearchFragmentArgs.fromBundle(getArguments());
        filterName = args.getFilterName();
        filterType = args.getFilterType();
        if (filterName != null && filterType != null) {
            if (filterType.equals("category"))
                presenter.loadFilteredCategoriess(filterName);
            else if (filterType.equals("ingredient"))
                presenter.loadFilteredIngredient(filterName);
            else if (filterType.equals("area"))
                presenter.loadFilteredArea(filterName);
        }


        // Set up filter button actions
        filterCategory.setOnClickListener(v -> {
            setupRecyclerView(recyclerView2, filterAdapterCategory);
            Log.i(TAG, "filterCategory clicked");
        });
        filterIngredient.setOnClickListener(v -> {
            setupRecyclerView(recyclerView2, filterAdapterIngredient);
            Log.i(TAG, "filterIngredient clicked");
        });
        filterArea.setOnClickListener(v -> {
            setupRecyclerView(recyclerView2, mealAreaFilterAdapter);
            Log.i(TAG, "filterArea clicked");
        });
    }

    private void updateSuggestions(String query) {
        List<Object> suggestions = new ArrayList<>();
        presenter.loadFilteredByName(query);
        if (query != null && !query.isEmpty()) {
            presenter.loadFilteredByName(query);
            addFilteredSuggestions(suggestions, query, mealsSearchList);
            addFilteredSuggestions(suggestions, query, categoryFilterList);
            addFilteredSuggestions(suggestions, query, ingredientFilterList);
            addFilteredSuggestions(suggestions, query, areaFilterList);

            if (suggestions.isEmpty()) {
                suggestionsRecyclerView.setVisibility(View.GONE);
                fallbackMessage.setVisibility(View.VISIBLE);
            } else {
                suggestionsRecyclerView.setVisibility(View.VISIBLE);
                fallbackMessage.setVisibility(View.GONE);
                suggestionsAdapter.updateSuggestions(suggestions);
            }
        } else {
            suggestionsRecyclerView.setVisibility(View.GONE);
            fallbackMessage.setVisibility(View.GONE);
        }
    }

    private <T> void addFilteredSuggestions(List<Object> suggestions, String query, ArrayList<T> list) {
        for (T item : list) {

            String itemName = getItemName(item);
            if (itemName.toLowerCase().contains(query.toLowerCase())) {
                suggestions.add(item);
            }
        }
    }

    private <T> String getItemName(T item) {
        if (item instanceof MealDTO) {
            return ((MealDTO) item).getStrMeal();
        } else if (item instanceof MealCategory) {
            return ((MealCategory) item).getStrCategory();
        } else if (item instanceof MealIngredient) {
            return ((MealIngredient) item).getStrIngredient();
        } else if (item instanceof MealArea) {
            return ((MealArea) item).getStrArea();
        }
        return "";
    }

    private <T> FilterAdapter<T> createFilterAdapter(List<T> filterList, Class<T> type) {
        return new FilterAdapter<>(
                getContext(),
                filterList,
                meal -> {
                    if (type.equals(MealCategory.class)) {
                        presenter.loadFilteredCategoriess(((MealCategory) meal).getStrCategory());
                        Log.i(TAG, "Category selected: " + ((MealCategory) meal).getStrCategory());
                        Toast.makeText(getContext(), ((MealCategory) meal).getStrCategory(), Toast.LENGTH_SHORT).show();
                    } else if (type.equals(MealIngredient.class)) {
                        presenter.loadFilteredIngredient(((MealIngredient) meal).getStrIngredient());
                        Toast.makeText(getContext(), ((MealIngredient) meal).getStrIngredient(), Toast.LENGTH_SHORT).show();
                    } else if (type.equals(MealArea.class)) {
                        presenter.loadFilteredArea(((MealArea) meal).getStrArea());
                        Toast.makeText(getContext(), ((MealArea) meal).getStrArea(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showData(List data) {
        if (data == null || data.isEmpty()) return;

        if (data.get(0) instanceof MealCategory) {
            categoryFilterList.clear();
            categoryFilterList.addAll((List<MealCategory>) data);
            filterAdapterCategory.updateFilterList(categoryFilterList);
        } else if (data.get(0) instanceof MealIngredient) {
            ingredientFilterList.clear();
            ingredientFilterList.addAll((List<MealIngredient>) data);
            filterAdapterIngredient.updateFilterList(ingredientFilterList);
        } else if (data.get(0) instanceof MealArea) {
            areaFilterList.clear();
            areaFilterList.addAll((List<MealArea>) data);
            mealAreaFilterAdapter.updateFilterList(areaFilterList);
        } else if (data.get(0) instanceof MealDTO) {

            mealsList.clear();
            mealsList.addAll((List<MealDTO>) data);
            mealsSearchList.clear();
            mealsSearchList.addAll((List<MealDTO>) data);
            Log.i(TAG, "showData: " + mealsList.size());
            allMealPagerAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSeeMoreClick(String id) {
        Log.i(TAG, "onSeeMoreClick: " + id);
        if (id != null) {
            if (mode!=null&&mode.equals("guest")) {
                showRestrictedAccessDialog();

            }else
            {
                SearchFragmentDirections.ActionSearchFragmentToAllMealDetailsFragment action = SearchFragmentDirections.actionSearchFragmentToAllMealDetailsFragment(id, "searchFragment");
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(action);
            }

        }

    }

    // Method to display the restricted access popup
    private void showRestrictedAccessDialog() {

        title.setText(R.string.sign_up_for_more_features);
        message.setText(R.string.add_your_food_preferences_plan_your_meals_and_more);
        goButton.setText(R.string.sign_up);
        cancelButton.setText(R.string.cancel);

        goButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("destination_fragment", "startFragment");

            // Start the MainActivity
            if (getContext() != null) {
                getContext().startActivity(intent);

                // Finish the current Activity if this method is called within an Activity
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}