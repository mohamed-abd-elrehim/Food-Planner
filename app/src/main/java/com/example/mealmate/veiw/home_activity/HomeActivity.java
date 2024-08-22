package com.example.mealmate.veiw.home_activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mealmate.R;
import com.example.mealmate.veiw.main_activity.MainActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private DrawerLayout drawerLayout;
    private Intent intent;
    private String extraValue;
    private boolean isDialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        TextView name = headerView.findViewById(R.id.profile_name);
        TextView email = headerView.findViewById(R.id.profile_email);


        // Correctly initialize BottomNavigationView and BottomAppBar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationView);
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment2);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            if (destination.getId() == R.id.mealDetailView) {
//                bottomAppBar.setVisibility(View.GONE);
//            } else {
//                bottomAppBar.setVisibility(View.VISIBLE);
//            }
        });

        // Handle the extra data
        if ("guest".equals(extraValue)) {
// Set a listener for item selections in the BottomNavigationView
            bottomNavigationView.setOnItemSelectedListener(item -> {
                // Check if the user is a guest
                if ("guest".equals(extraValue)) {
                    // Determine which item was clicked
                    if (item.getItemId() == R.id.nav_addplanmeal ||
                            item.getItemId() == R.id.favoriteMealsFragment ||
                            item.getItemId() == R.id.planOfTheWeekFragment) {

                        // Display the restricted access popup
                        showRestrictedAccessDialog();

                        // Return false to indicate the click was handled and not to proceed with navigation
                        return false;
                    }
                }

                // Allow navigation to proceed for other cases
                return NavigationUI.onNavDestinationSelected(item, navController)
                        || super.onOptionsItemSelected(item);
            });

        } else {
            // Fetch user data from Firebase
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            Log.i(TAG, "onCreate: " + firebaseAuth.getCurrentUser().getUid());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = firebaseAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name.setText(document.getString("name"));
                        email.setText(firebaseAuth.getCurrentUser().getEmail());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });

            // Handle menu item clicks
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_logout) {
                    firebaseAuth.signOut();
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("destination_fragment", "startFragment");
                    startActivity(intent);

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
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // If the user is a guest and the dialog is not shown, prevent sliding
                if ("guest".equals(extraValue) && !isDialogShown) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    showRestrictedAccessDialog();
                    isDialogShown = true; // Set flag to true to prevent multiple dialog displays
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
        new AlertDialog.Builder(this)
                .setTitle("Sign Up for More Features")
                .setMessage("Add your food preferences ,plan your meals and more!")
                .setPositiveButton("Sign Up", (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("destination_fragment", "startFragment");;
                    startActivity(intent);
                    this.finish();
                })
                .setNegativeButton("CANCEL", (dialog, which) -> {
                    dialog.dismiss();
                    isDialogShown = false; // Reset the flag when the dialog is dismissed
                })
                .show();
    }
}
