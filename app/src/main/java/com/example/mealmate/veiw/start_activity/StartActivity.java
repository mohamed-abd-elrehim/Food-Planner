package com.example.mealmate.veiw.start_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    // Access UI elements in the custom layout
    TextView title ;
    TextView message;
    Button goButton ;
    Button cancelButton ;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);



        // Create an instance of AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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


                title.setText(R.string.no_internet_connection);
                message.setText(R.string.you_need_an_internet_connection_to_proceed_the_app_will_now_close);
                goButton.setText(R.string.ok);
                cancelButton.setVisibility(View.GONE);

                goButton.setOnClickListener(v -> {
                    this.finish();
                });

                dialog.show();


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