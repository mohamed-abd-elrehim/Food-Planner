package com.example.mealmate.presenter.splash_fragment_presenter;

import com.example.mealmate.veiw.splash_fragment.splash_fragment_interface.Splash_Fragment_Interface;

public class Splash_Fragment_Presenter {

        private final Splash_Fragment_Interface view;

        public Splash_Fragment_Presenter(Splash_Fragment_Interface view) {
            this.view = view;
        }

        public void onStartButtonClicked() {
            view.navigateToStartFragment();
        }

}


