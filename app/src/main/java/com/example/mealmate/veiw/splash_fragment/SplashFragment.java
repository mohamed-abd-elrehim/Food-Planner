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

public class SplashFragment extends Fragment {
    private Button start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Use a safe way to get the NavController
        NavController navController = Navigation.findNavController(view);
        start.setOnClickListener(v -> {
            navController.navigate(R.id.action_splashFragment_to_startFragment);
        });


    }
}
