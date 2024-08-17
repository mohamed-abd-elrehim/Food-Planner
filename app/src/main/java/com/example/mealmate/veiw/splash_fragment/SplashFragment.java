package com.example.mealmate.veiw.splash_fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mealmate.R;
import com.example.mealmate.presenter.splash_fragment_presenter.Splash_Fragment_Presenter;
import com.example.mealmate.veiw.splash_fragment.splash_fragment_interface.Splash_Fragment_Interface;

public class SplashFragment extends Fragment implements Splash_Fragment_Interface {
    private Button start;
    private Splash_Fragment_Presenter presenter;
    private NavController navController;

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
        start = view.findViewById(R.id.startBTU);
        navController = Navigation.findNavController(view);

        start.setOnClickListener(v -> presenter.onStartButtonClicked());
    }

    @Override
    public void navigateToStartFragment() {
        navController.navigate(R.id.action_splashFragment_to_startFragment);
    }
}