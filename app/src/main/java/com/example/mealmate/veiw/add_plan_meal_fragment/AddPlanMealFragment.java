package com.example.mealmate.veiw.add_plan_meal_fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class AddPlanMealFragment extends Fragment implements AddPlanMealFragmentVeiwInterface {

    private ImageView mealPlanImage;
    private TextView mealPlanDateVeiw;
    private TextView weekTextView;
    private TextView weekRangeTextView;

    private Spinner mealTypeSpinner;
    private Button mealPlanAddButton;

    private String mealID = null;
    private String mealImgURL = null;
    private NavController navController;


    private static final String TAG = "AddPlanMealFragment";
    private AddPlanMealFragmentPresenter presenter;

    private MealPlan mealPlan;
    private CustomMeal customMeal;
    private Button selectedButton = null;


    private Calendar currentWeek;
    private SimpleDateFormat dateFormat;

    private Button butMonday, butTuesday, butWednesday, butThursday, butFriday, butSaturday, butSunday;
    private List<String> lsitAvailableDays;


    private Calendar today;
    private ImageButton prevWeekButton;
    private ImageButton nextWeekButton;

    private String selectedWeekRange = "Select Meal Type";
    private String selectedDay = null;
    private String selectedMealType = null;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_plan_meal, container, false);
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

        // Initialize UI components
        initializeUIComponents(view);
        lsitAvailableDays = new ArrayList<>();

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
        }

        // Initialize the presenter
        presenter = new AddPlanMealFragmentPresenter(
                MealRepository.getInstance(
                        LocalDataSourceImpl.getInstance(
                                AppDataBase.getInstance(getContext()).getFavoriteMealDAO(),
                                AppDataBase.getInstance(getContext()).getMealPlanDAO()
                        ),
                        RemoteDataSourceImpl.getInstance()
                ),
                this
        );

        // Load meal details by ID
        presenter.loadAllMealDetailsById(mealID);

        // Set item selected listener for Spinner
        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMealType = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMealType="Select Meal Type";
            }
        });

        dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Africa/Cairo"));
        Date currentDate = new Date();

        String formattedDate = dateFormat.format(currentDate);


        Log.i("mohammed", "onViewCreated: "+formattedDate);

        // Initialize the current week starting from Monday
        currentWeek = Calendar.getInstance();
        currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Save today's date for comparison
        today = Calendar.getInstance();
        today.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Display the current week range
        presenter.updateWeekRange(currentWeek, dateFormat);

        prevWeekButton.setOnClickListener(v -> {
            // Move to the previous week only if it's not before the current week
            currentWeek.add(Calendar.WEEK_OF_YEAR, -1);
            Log.i(TAG, "prevWeekButton: 1");
            if (currentWeek.before(today)) {
                currentWeek.add(Calendar.WEEK_OF_YEAR, 1); // revert to the current week
                Log.i(TAG, "prevWeekButton: 2");
            } else {
                Log.i(TAG, "prevWeekButton: 3");
                presenter.updateWeekRange(currentWeek, dateFormat);
            }
        });

        nextWeekButton.setOnClickListener(v -> {
            // Move to the next week
            currentWeek.add(Calendar.WEEK_OF_YEAR, 1);
            presenter.updateWeekRange(currentWeek, dateFormat);
        });

        // Set onClickListener for Add Button
        mealPlanAddButton.setOnClickListener(v -> {

            // Create MealPlan if valid inputs
            if (customMeal != null&& selectedDay != null && !selectedMealType.equals("Select Meal Type") && selectedWeekRange != null) {
                mealPlan = new MealPlan(
                        mealID,
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        selectedDay,
                        selectedMealType,
                        selectedWeekRange
                );
                presenter.addMealToPaln(mealPlan, customMeal);
                Toast.makeText(getContext(), R.string.added_to_your_plans, Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_addPlanMealFragment_to_planOfTheWeekFragment2);
            } else {

                title.setText(R.string.incomplete_data);
                message.setText(R.string.please_enter_all_required_data_before_proceeding);
                goButton.setText(R.string.ok);
                cancelButton.setVisibility(View.GONE);

                goButton.setOnClickListener(vi -> {
                    dialog.dismiss();
                });
                dialog.show();
            }


        });
    }


    private void initializeUIComponents(View view) {
        mealPlanImage = view.findViewById(R.id.mealPlanImage);
        mealPlanDateVeiw = view.findViewById(R.id.mealPlanDateView);
        mealPlanAddButton = view.findViewById(R.id.mealPlanAddButton);
        mealTypeSpinner = view.findViewById(R.id.mealTypeSpinner);
        navController = Navigation.findNavController(view);

        butMonday = view.findViewById(R.id.btnMonday);
        butTuesday = view.findViewById(R.id.btnTuesday);
        butWednesday = view.findViewById(R.id.btnWednesday);
        butThursday = view.findViewById(R.id.btnThursday);
        butFriday = view.findViewById(R.id.btnFriday);
        butSaturday = view.findViewById(R.id.btnSaturday);
        butSunday = view.findViewById(R.id.btnSunday);

        butMonday.setOnClickListener(v -> selectDay("Monday", butMonday));
        butTuesday.setOnClickListener(v -> selectDay("Tuesday", butTuesday));
        butWednesday.setOnClickListener(v -> selectDay("Wednesday", butWednesday));
        butThursday.setOnClickListener(v -> selectDay("Thursday", butThursday));
        butFriday.setOnClickListener(v -> selectDay("Friday", butFriday));
        butSaturday.setOnClickListener(v -> selectDay("Saturday", butSaturday));
        butSunday.setOnClickListener(v -> selectDay("Sunday", butSunday));

        weekRangeTextView = view.findViewById(R.id.weekRangeTextView);
        prevWeekButton = view.findViewById(R.id.prevWeekButton);
        nextWeekButton = view.findViewById(R.id.nextWeekButton);
    }


    @Override
    public void showData(List<CustomMeal> data) {
        customMeal = data.get(0); // Ensure customMeal is updated

    }

    @Override
    public void showError(String errorMessage) {
        // Implement as needed
    }


    // Method to set available days; should be called when you determine the availability
    public void setAvailableDays(List<String> days) {
        lsitAvailableDays.clear();
        lsitAvailableDays.addAll(days);
        Log.i(TAG, "setAvailableDays: " + lsitAvailableDays.toString());
    }


    public void updateDayButtons() {
        Log.i(TAG, "updateDayButtons: " + lsitAvailableDays.toString());
        // Create a map to associate day names with their corresponding buttons
        Map<String, Button> dayButtonMap = new HashMap<>();
        dayButtonMap.put("Monday", butMonday);
        dayButtonMap.put("Tuesday", butTuesday);
        dayButtonMap.put("Wednesday", butWednesday);
        dayButtonMap.put("Thursday", butThursday);
        dayButtonMap.put("Friday", butFriday);
        dayButtonMap.put("Saturday", butSaturday);
        dayButtonMap.put("Sunday", butSunday);


        // Disable all buttons initially
        for (Button button : dayButtonMap.values()) {
            button.setEnabled(false);
        }

        // Enable buttons for days present in the availableDays list
        for (String day : lsitAvailableDays) {
            Button button = dayButtonMap.get(day);
            if (button != null) {
                button.setEnabled(true);
            }
        }
    }

    @Override
    public void updateWeekRangeText(String weekRange) {
        weekRangeTextView.setText(weekRange);
        selectedWeekRange = weekRange;

    }

    private void selectDay(String day, Button button) {
        resetButtonBackgrounds();
        button.setBackgroundResource(R.drawable.day_button_backgroundv2);
        selectedDay = day;
    }

    private void resetButtonBackgrounds() {
        butMonday.setBackgroundResource(R.drawable.day_button_background);
        butTuesday.setBackgroundResource(R.drawable.day_button_background);
        butWednesday.setBackgroundResource(R.drawable.day_button_background);
        butThursday.setBackgroundResource(R.drawable.day_button_background);
        butFriday.setBackgroundResource(R.drawable.day_button_background);
        butSaturday.setBackgroundResource(R.drawable.day_button_background);
        butSunday.setBackgroundResource(R.drawable.day_button_background);
    }

}