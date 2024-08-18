package com.example.mealmate.veiw.main_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;

import com.example.mealmate.R;


public class MainActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Check if there is a destination fragment passed via the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("destination_fragment")) {
            String destinationFragment = intent.getStringExtra("destination_fragment");
            navigateToFragment(destinationFragment);
        }


    }

    private void navigateToFragment(String fragmentName) {
        if ("loginFragment".equals(fragmentName)) {
            navController.navigate(R.id.loginFragment);
        }
        }
}