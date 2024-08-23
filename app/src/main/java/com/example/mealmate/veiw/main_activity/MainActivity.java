package com.example.mealmate.veiw.main_activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mealmate.R;
import com.example.mealmate.receiver.NetworkChangeReceiver;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize NetworkChangeReceiver
        networkChangeReceiver = new NetworkChangeReceiver(this::handleNetworkChange);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        // Initialize NavController
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
        } else if ("startFragment".equals(fragmentName)) {
            navController.navigate(R.id.startFragment);
        }
    }

    private void handleNetworkChange(boolean isConnected) {
        if (!isConnected && FirebaseAuth.getInstance().getCurrentUser() == null) {
            runOnUiThread(() -> {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("No Internet Connection")
                            .setMessage("You need an internet connection to proceed. The app will now close.")
                            .setPositiveButton("OK", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();

            });
        }
    }


}
