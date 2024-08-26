package com.example.mealmate.veiw.signup_fragment.signup_fragment_view_interface;

import com.google.firebase.auth.FirebaseUser;

public interface SignUpFragmentViewInterface {
    void showLoading();
    void hideLoading();
    void onSignUpSuccess(String message);
    void onSignupFailure(String message);
}
