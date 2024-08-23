package com.example.mealmate.veiw.add_plan_meal_fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mealmate.presenter.add_plan_meal_fragment_presenter.AddPlanMealFragmentPresenter;
import com.example.mealmate.veiw.add_plan_meal_fragment.add_plan_meal_fragment_veiw_interface.AddPlanMealFragmentVeiwInterface;
import com.example.mealmate.R;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.model.network.RemoteDataSourceImpl;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPlanMealFragment extends Fragment implements AddPlanMealFragmentVeiwInterface {

    private ImageView mealPlanImage;
    private TextView mealPlanDateVeiw;
    private CalendarView mealPlanCalendar;
    private TextView mealPlanTimeVeiw;
    private TimePicker mealPlanTimePicker;
    private Spinner mealTypeSpinner;
    private Button mealPlanAddButton;

    private String mealID = null;
    private String mealImgURL = null;

    private String mealType = null;
    private String formattedDate = null;
    private String formattedTime = null;
    private String dayName = null;

    private static final String TAG = "AddPlanMealFragment";
    private AddPlanMealFragmentPresenter presenter;

    private MealPlan mealPlan;
    private CustomMeal customMeal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_plan_meal, container, false);
    }

    /*
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Initialize UI components
            mealPlanImage = view.findViewById(R.id.mealPlanImage);
            mealPlanDateVeiw = view.findViewById(R.id.mealPlanDateVeiw);
            mealPlanCalendar = view.findViewById(R.id.mealPlanCalendar);
            mealPlanTimeVeiw = view.findViewById(R.id.mealPlanTimeVeiw);
            mealPlanTimePicker = view.findViewById(R.id.mealPlanTimePicker);
            mealPlanAddButton = view.findViewById(R.id.mealPlanAddButton);
            mealTypeSpinner = view.findViewById(R.id.mealTypeSpinner);

            // Set up the Spinner with the array of meal types
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.meal_types, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mealTypeSpinner.setAdapter(adapter);
            Log.i(TAG, "onViewCreated 1: "+mealType +" " +formattedDate+ " "+formattedTime+ " "+dayName);
            Log.i(TAG, "onViewCreated: 1"+ mealID +" "+mealImgURL);
            // Load meal image if available
            mealID = AddPlanMealFragmentArgs.fromBundle(getArguments()).getMeal();
            mealImgURL = AddPlanMealFragmentArgs.fromBundle(getArguments()).getMealImgURL();
            Log.i(TAG, "onViewCreated: 2"+ mealID +" "+mealImgURL);
            if (mealID != null && mealImgURL != null) {
                Glide.with(this).load(mealImgURL).into(mealPlanImage);
            }

            // Initialize the presenter
            presenter = new AddPlanMealFragmentPresenter(
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
            presenter.loadAllMealDetailsById(mealID);

            // Set item selected listener for Spinner
            mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedMealType = parent.getItemAtPosition(position).toString();
                    // Show or hide TimePicker based on selected meal type
                    if (selectedMealType.equals("Select Meal Type")) {
                        mealPlanTimePicker.setVisibility(View.GONE);
                        mealPlanTimeVeiw.setVisibility(View.GONE);

                    } else {
                        mealPlanTimePicker.setVisibility(View.VISIBLE);
                        mealPlanTimeVeiw.setVisibility(View.VISIBLE);
                    }

                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            Log.i(TAG, "onViewCreated 2: "+mealType +" " +formattedDate+ " "+formattedTime+ " "+dayName);

            // Set listener for CalendarView to check validity
            mealPlanCalendar.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {

                long dateMillis = mealPlanCalendar.getDate();
                Date date = new Date(dateMillis);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                formattedDate = dateFormat.format(date);


                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                dayName = dayFormat.format(date);

            });
            Log.i(TAG, "onViewCreated 3: "+mealType +" " +formattedDate+ " "+formattedTime+ " "+dayName);

            // Set listener for TimePicker to check validity
            mealPlanTimePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {

                // Format the time as a string
                 formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

            });
            Log.i(TAG, "onViewCreated 4: "+mealType +" " +formattedDate+ " "+formattedTime+ " "+dayName);

            mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mealType = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle the case where no item is selected (optional)
                }
            });
            Log.i(TAG, "onViewCreated 5: "+mealType +" " +formattedDate+ " "+formattedTime+ " "+dayName);

            // Set onClickListener for Add Button
            mealPlanAddButton.setOnClickListener(v -> {
                if (mealPlan != null && customMeal != null&& mealType.isEmpty()|| mealType.equals("Select Meal Type")
                        &&formattedDate.isEmpty()&&formattedTime.isEmpty()&&dayName.isEmpty()) {
                    // If both mealPlan and customMeal are not null, call the presenter method
                    presenter.addMealToPaln(mealPlan, customMeal);
                    Log.i(TAG, "onViewCreated: ");
                } else {
                    // If either mealPlan or customMeal is null, show an alert dialog
                    new AlertDialog.Builder(getContext())
                            .setTitle("Incomplete Data")
                            .setMessage("Please enter all required data before proceeding.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

        }

    */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        mealPlanImage = view.findViewById(R.id.mealPlanImage);
        mealPlanDateVeiw = view.findViewById(R.id.mealPlanDateVeiw);
        mealPlanCalendar = view.findViewById(R.id.mealPlanCalendar);
        mealPlanTimeVeiw = view.findViewById(R.id.mealPlanTimeVeiw);
        mealPlanTimePicker = view.findViewById(R.id.mealPlanTimePicker);
        mealPlanAddButton = view.findViewById(R.id.mealPlanAddButton);
        mealTypeSpinner = view.findViewById(R.id.mealTypeSpinner);

        // Set up the Spinner with the array of meal types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);

        // Load meal image if available
        mealID = AddPlanMealFragmentArgs.fromBundle(getArguments()).getMeal();
        mealImgURL = AddPlanMealFragmentArgs.fromBundle(getArguments()).getMealImgURL();
        if (mealID != null && mealImgURL != null) {
            Glide.with(this)
                    .load(mealImgURL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mealPlanImage);

            //Glide.with(this).load(mealImgURL).into(mealPlanImage);
        }

        // Initialize the presenter
        presenter = new AddPlanMealFragmentPresenter(
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
        presenter.loadAllMealDetailsById(mealID);

        // Set item selected listener for Spinner
        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mealType = parent.getItemAtPosition(position).toString();
                // Show or hide TimePicker based on selected meal type
                if (mealType.equals("Select Meal Type")) {
                    mealPlanTimePicker.setVisibility(View.GONE);
                    mealPlanTimeVeiw.setVisibility(View.GONE);
                } else {
                    mealPlanTimePicker.setVisibility(View.VISIBLE);
                    mealPlanTimeVeiw.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no item is selected (optional)
            }
        });

        // Set listener for CalendarView to check validity
        mealPlanCalendar.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Corrected method to get the selected date
            formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            dayName = dayFormat.format(new Date(year - 1900, month, dayOfMonth)); // Adjust for year offset
        });

        // Set listener for TimePicker to check validity
        mealPlanTimePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            // Format the time as a string
            formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
        });

        // Set onClickListener for Add Button
        mealPlanAddButton.setOnClickListener(v -> {
            if (mealType != null && !mealType.equals("Select Meal Type")
                    && formattedDate != null && !formattedDate.isEmpty()
                    && formattedTime != null && !formattedTime.isEmpty()
                    && dayName != null && !dayName.isEmpty()) {

                // Create MealPlan if valid inputs
                if (mealPlan != null && customMeal != null) {
                    mealPlan = new MealPlan(mealID,
                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            dayName, mealType,
                            formattedDate + " T: " + formattedTime);
                    presenter.addMealToPaln(mealPlan, customMeal);
                }
            } else {
                // If inputs are invalid, show an alert dialog
                new AlertDialog.Builder(getContext())
                        .setTitle("Incomplete Data")
                        .setMessage("Please enter all required data before proceeding.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }


    @Override
    public void showData(List<CustomMeal> data) {
        customMeal = data.get(0); // Ensure customMeal is updated
        if (customMeal != null) {
            mealPlan = new MealPlan(
                    mealID,
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    dayName,
                    mealType,
                    formattedDate + " T: " + formattedTime
            );
            Log.i(TAG, "showData: "+mealPlan);

        }
    }

    @Override
    public void showError(String errorMessage) {
        // Implement as needed
    }
}
