package com.example.mealmate.veiw.main_activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    // Access UI elements in the custom layout
    TextView title ;
    TextView message;
    Button goButton ;
    Button cancelButton ;
    AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create an instance of AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        // Set the custom layout to the dialog
        builder.setView(dialogView);
        // Create and show the dialog
        dialog = builder.create();

        // Initialize NetworkChangeReceiver
        networkChangeReceiver = new NetworkChangeReceiver(this::handleNetworkChange);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
        title = dialogView.findViewById(R.id.custom_title);
        message = dialogView.findViewById(R.id.custom_message);
        goButton = dialogView.findViewById(R.id.button_go);
        cancelButton = dialogView.findViewById(R.id.button_cancel);
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

                title.setText(R.string.no_internet_connection);
                message.setText(R.string.you_need_an_internet_connection_to_proceed_or_stay_on_offline_mode);
                goButton.setText(R.string.ok);
                cancelButton.setVisibility(View.GONE);
                goButton.setOnClickListener(v -> {
                    finish();
                });

                dialog.show();

            });
        }
    }


}
