package com.example.mealmate.veiw.main_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mealmate.R;
import com.example.mealmate.veiw.home_activity.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize NavController
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

//        // Check if the user is logged in
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            // User is logged in, navigate to the home screen
//            navigateToHomeScreen();
//        } else {
//            // User is not logged in, navigate to the login screen
//            navigateToStartScreen();
//        }
//
        // Check if there is a destination fragment passed via the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("destination_fragment")) {
            String destinationFragment = intent.getStringExtra("destination_fragment");
            navigateToFragment(destinationFragment);
        }
    }
//
    private void navigateToFragment(String fragmentName) {
        if ("loginFragment".equals(fragmentName)) {
            navController.navigate(R.id.loginFragment);

        }else if ("startFragment".equals(fragmentName)) {
            navController.navigate(R.id.startFragment);

        }
    }
//
//    private void navigateToHomeScreen() {
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
//
//    private void navigateToStartScreen() {
//        navController.navigate(R.id.loginFragment);
//    }
}
