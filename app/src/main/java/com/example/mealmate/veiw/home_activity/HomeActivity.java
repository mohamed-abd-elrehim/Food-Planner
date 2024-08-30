package com.example.mealmate.veiw.home_activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mealmate.presenter.home_activity_presenter.DataPresenter;
import com.example.mealmate.R;
import com.example.mealmate.model.MealRepository.MealRepository;
import com.example.mealmate.model.database.AppDataBase;
import com.example.mealmate.model.database.local_data_source.LocalDataSourceImpl;
import com.example.mealmate.model.network.RemoteDataSourceImpl;
import com.example.mealmate.presenter.home_activity_presenter.Home_Activity_Presenter;
import com.example.mealmate.receiver.NetworkChangeReceiver;
import com.example.mealmate.utils.NetworkUtils;
import com.example.mealmate.veiw.home_activity.home_activity_interface.Home_Activity_Interface;
import com.example.mealmate.veiw.main_activity.MainActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity implements Home_Activity_Interface {
    private static final String TAG = "HomeActivity";
    private DrawerLayout drawerLayout;
    private Intent intent;
    private String extraValue;
    private boolean isDialogShown = false;
    private CoordinatorLayout coordinatorLayout;
    private boolean isNetworkAvailable;
    private NetworkChangeReceiver networkChangeReceiver;
    NavController navController;
    BottomNavigationView bottomNavigationView;
    private boolean isReloaded;
    DataPresenter dataPresenter;

    // Access UI elements in the custom layout
    TextView title;
    TextView message;
    Button goButton;
    Button cancelButton;
    AlertDialog dialog;
    private Home_Activity_Presenter presenter;
    TextView name;
    TextView email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        presenter = new Home_Activity_Presenter(this);


        // Create an instance of AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        // Set the custom layout to the dialog
        builder.setView(dialogView);
        // Create and show the dialog
        dialog = builder.create();

        dataPresenter = new DataPresenter(
                MealRepository.getInstance(
                        LocalDataSourceImpl.getInstance(
                                AppDataBase.getInstance(this).getFavoriteMealDAO(),
                                AppDataBase.getInstance(this).getMealPlanDAO()
                        ),
                        RemoteDataSourceImpl.getInstance()),
                this
                );
        // Initialize NetworkChangeReceiver
        networkChangeReceiver = new NetworkChangeReceiver(this::handleNetworkChange);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        // Retrieve the Intent that started this activity
        intent = getIntent();
        // Extract the extra data
        extraValue = intent.getStringExtra("user_type");
        Log.i(TAG, "onCreate: " + extraValue);


        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up the navigation drawer header
        View headerView = navigationView.getHeaderView(0);
         name = headerView.findViewById(R.id.profile_name);
         email = headerView.findViewById(R.id.profile_email);
        coordinatorLayout = findViewById(R.id.snakeBar);


        // Correctly initialize BottomNavigationView and BottomAppBar
        bottomNavigationView = findViewById(R.id.bottom_navigationView);
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        title = dialogView.findViewById(R.id.custom_title);
        message = dialogView.findViewById(R.id.custom_message);
        goButton = dialogView.findViewById(R.id.button_go);
        cancelButton = dialogView.findViewById(R.id.button_cancel);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment2);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        if (NetworkUtils.isNetworkAvailable(this)) {
            isReloaded = true;
        } else {
            isReloaded = false;
        }
        if ("guest".equals(extraValue)) {
            showSignupDialog();
        }

        bottomNavigationView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                bottomNavigationView.getWindowVisibleDisplayFrame(r);
                if (bottomNavigationView.getRootView().getHeight() - (r.bottom - r.top) > 500) { // if more than 100 pixels, its probably a keyboard...
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.nav_addplanmeal) {
                    if (!NetworkUtils.isNetworkAvailable(HomeActivity.this)) {
                        showNetWorkDialog();
                        return false;
                    }
                    if ("guest".equals(extraValue)) {
                        coordinatorLayout.setVisibility(View.GONE);
                        navController.navigate(R.id.searchFragment);
                        return true;
                    }
                    // Navigate to SearchFragment
                    navController.navigate(R.id.searchFragment);
                    return true;
                } else if (item.getItemId() == R.id.homeFragment) {
                    if (!NetworkUtils.isNetworkAvailable(HomeActivity.this)) {
                        showNetWorkDialog();
                        return false;
                    }

                    if ("guest".equals(extraValue)) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                        navController.navigate(R.id.homeFragment);
                        return true;
                    }

                    // Navigate to HomeFragment
                    navController.navigate(R.id.homeFragment);
                    return true;
                } else if (item.getItemId() == R.id.searchFragment) {
                    if (!NetworkUtils.isNetworkAvailable(HomeActivity.this)) {
                        showNetWorkDialog();
                        return false;
                    }
                    if ("guest".equals(extraValue)) {
                        coordinatorLayout.setVisibility(View.GONE);
                        navController.navigate(R.id.searchFragment);
                        return true;
                    }
                    // Navigate to SearchFragment
                    navController.navigate(R.id.searchFragment);
                    return true;
                } else if (item.getItemId() == R.id.favoriteMealsFragment) {

                    if ("guest".equals(extraValue)) {
                        showRestrictedAccessDialog();
                        return false;
                    }
                    // Navigate to FavoriteMealsFragment
                    navController.navigate(R.id.favoriteMealsFragment);
                    return true;
                } else if (item.getItemId() == R.id.planOfTheWeekFragment) {

                    if ("guest".equals(extraValue)) {
                        showRestrictedAccessDialog();
                        return false;
                    }
                    // Navigate to PlanOfTheWeekFragment
                    navController.navigate(R.id.planOfTheWeekFragment);
                    return true;
                }
                return false;
            }
        });


        // Handle the extra data
        if ("guest".equals(extraValue)) {
            showSignupDialog();
            coordinatorLayout.setVisibility(View.VISIBLE);
            // Set a listener for item selections in the BottomNavigationView

        } else {
            coordinatorLayout.setVisibility(View.GONE);

              presenter.getUserData();

            // Handle menu item clicks
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_logout) {

                    title.setText(R.string.logout_confirmation);
                    message.setText(R.string.are_you_sure_you_want_to_log_out);
                    goButton.setText(R.string.yes);
                    cancelButton.setText(R.string.no);

                    goButton.setOnClickListener(v -> {
                        presenter.logout();
                        Toast.makeText(this, R.string.logged_out, Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("destination_fragment", "startFragment");
                        startActivity(intent);
                    });
                    cancelButton.setOnClickListener(v -> {
                        dialog.dismiss();
                    });
                    dialog.show();


                } else if (id == R.id.nav_beckup) {
                    dataPresenter.backupData();
                    Toast.makeText(this, "Starting backup process. Please wait...", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_restore) {
                    dataPresenter.restoreData();
                    Toast.makeText(this, "Backup process initiated. This may take a few moments...", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });

        }


        // Set up the toggle button for opening and closing the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Check if the user is a guest
                if ("guest".equals(extraValue) && !isDialogShown) {
                    // Prevent the drawer from staying open and show a dialog instead
                    drawerLayout.closeDrawer(GravityCompat.START);
                    showRestrictedAccessDialog();
                    isDialogShown = true; // Set flag to true to prevent multiple dialog displays
                }
                if (!NetworkUtils.isNetworkAvailable(HomeActivity.this) && !isDialogShown) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    showNetWorkDialog();
                    isDialogShown = true;
                }

            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // If the user is a guest and the dialog is not shown, prevent sliding
                if ("guest".equals(extraValue) && !isDialogShown) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    showRestrictedAccessDialog();
                    isDialogShown = true; // Set flag to true to prevent multiple dialog displays
                }
                if (!NetworkUtils.isNetworkAvailable(HomeActivity.this) && !isDialogShown) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    showNetWorkDialog();
                    isDialogShown = true;
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // Method to display the restricted access popup
    private void showRestrictedAccessDialog() {

        title.setText(R.string.sign_up_for_more_features);
        message.setText(R.string.add_your_food_preferences_plan_your_meals_and_more);
        goButton.setText(R.string.sign_up);
        cancelButton.setText(R.string.cancel);

        goButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("destination_fragment", "startFragment");
            startActivity(intent);
            this.finish();
        });
        cancelButton.setOnClickListener(v -> {
            isDialogShown = false;
            dialog.dismiss();
        });
        dialog.show();
    }


    private void showSignupDialog() {
        coordinatorLayout.setVisibility(View.VISIBLE);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.sign_up_to_personalize_your_recipe_feed, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.get_started, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle the action click event
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.putExtra("destination_fragment", "startFragment");
                        startActivity(intent);
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent)); // Use colorAccent here

        snackbar.show();
    }

    // Method to display the restricted access popup
    private void showRestrictedNetWorkDialog() {
        title.setText(R.string.open_internet_connection_for_more_features);
        message.setText(R.string.add_your_food_preferences_plan_your_meals_and_more);
        goButton.setText(R.string.ok);
        cancelButton.setVisibility(View.GONE);

        goButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();

    }

    // Method to display the restricted access popup
    private void showNetWorkDialog() {

        title.setText(R.string.no_internet_connection);
        message.setText(R.string.you_need_an_internet_connection_to_proceed_or_stay_on_offline_mode);
        goButton.setText(R.string.ok);
        cancelButton.setVisibility(View.GONE);

        goButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();

    }


    private void handleNetworkChange(boolean isConnected) {
        if (!isConnected) {
            if ("guest".equals(extraValue)) {
                runOnUiThread(() -> {
                    new Handler().postDelayed(() -> {

                        title.setText(R.string.no_internet_connection);
                        message.setText(R.string.you_need_an_internet_connection_to_proceed_or_stay_on_offline_mode);
                        goButton.setText(R.string.ok);
                        cancelButton.setVisibility(View.GONE);

                        goButton.setOnClickListener(v -> {
                            finish();
                        });
                        dialog.show();


                    }, 100); // Adjust the delay as needed
                });
            } else {
                isDialogShown = false;
                runOnUiThread(() -> {
                    navController.navigate(R.id.favoriteMealsFragment);
                    new Handler().postDelayed(() -> {


                        title.setText(R.string.no_internet_connection);
                        message.setText(R.string.you_need_an_internet_connection_to_proceed_or_stay_on_offline_mode);
                        goButton.setText(R.string.ok);
                        cancelButton.setVisibility(View.GONE);

                        goButton.setOnClickListener(v -> {
                            dialog.dismiss();
                            isDialogShown = false;
                        });
                        dialog.show();


                    }, 100); // Adjust the delay as needed
                });
            }
        } else {
            if (!isReloaded) {
                Toast.makeText(this, R.string.internet_connection_restored, Toast.LENGTH_SHORT).show();
                isReloaded = true; // Set flag to true to prevent multiple reloads
                this.recreate(); // Reload the activity
            }
        }
    }


    @Override
    public void updateProfile(String name, String email) {
        this.email.setText(email);
        this.name.setText(name);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }
}





