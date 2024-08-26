package com.example.mealmate.veiw.login_fragment.login_fragment_view_Interface;


import com.google.firebase.auth.FirebaseUser;

public interface LoginFragmentViewInterface {
    void showLoading();
    void hideLoading();
    void onLoginSuccess(String message);
    void onLoginFailure(String message);
    void onUserDataSaveFailure(String errorMessage);
    void saveUserDetails(FirebaseUser user);
}
