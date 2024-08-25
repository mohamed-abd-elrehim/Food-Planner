package com.example.mealmate.veiw.start_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mealmate.R;
import com.example.mealmate.utils.NetworkUtils;
import com.example.mealmate.veiw.home_activity.HomeActivity;
import com.example.mealmate.veiw.main_activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {
    private Button start;
    private NavController navController;
    private LottieAnimationView lottieAnimationView;
    private static final int DELAY_MILLIS = 5000; // 5 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        lottieAnimationView = findViewById(R.id.lottie_animation_view);
        // Example: Start animation programmatically
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);

        // Use Handler to delay the navigation
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check if the user is logged in
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // User is logged in, navigate to the home screen
                navigateToHomeScreen();
            }  else if(NetworkUtils.isNetworkAvailable(this)) {
                // User is not logged in, navigate to the login screen
                navigateToStartScreen();
            }else if (!NetworkUtils.isNetworkAvailable(this)&&FirebaseAuth.getInstance().getCurrentUser()==null) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.no_internet_connection)
                        .setMessage(R.string.you_need_an_internet_connection_to_proceed_the_app_will_now_close)
                        .setPositiveButton(R.string.ok, (dialog, which) -> this.finish())
                        .setCancelable(false)
                        .show();

            }
        }, DELAY_MILLIS);

    }

    private void navigateToHomeScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }



    private void navigateToStartScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }
}