package com.example.mealmate.veiw.splash_fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mealmate.R;
import com.example.mealmate.presenter.splash_fragment_presenter.Splash_Fragment_Presenter;
import com.example.mealmate.veiw.home_activity.HomeActivity;
import com.example.mealmate.veiw.splash_fragment.splash_fragment_interface.Splash_Fragment_Interface;
import com.google.firebase.auth.FirebaseAuth;

public class SplashFragment extends Fragment implements Splash_Fragment_Interface {
    private Button start;
    private Splash_Fragment_Presenter presenter;
    private NavController navController;
    private LottieAnimationView lottieAnimationView;
    private static final int DELAY_MILLIS = 5000; // 5 seconds

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new Splash_Fragment_Presenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        lottieAnimationView = view.findViewById(R.id.lottie_animation_view);
        // Example: Start animation programmatically
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);


        // Use Handler to delay the navigation
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check if the user is logged in
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // User is logged in, navigate to the home screen
                navigateToHomeScreen();
            } else {
                // User is not logged in, navigate to the login screen
                navigateToStartScreen();
            }
        }, DELAY_MILLIS);
    }

    private void navigateToHomeScreen() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.getActivity().finish();
    }

    private void navigateToStartScreen() {
        Navigation.findNavController(requireView()).navigate(R.id.action_splashFragment_to_startFragment);
    }


    @Override
    public void navigateToStartFragment() {

    }
}