package com.example.mealmate.presenter.login_fragment_presenter.login_fragment_interface;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

public interface LoginFragmentPresenterInterface {
    void loginWithEmailAndPassword(String email, String password);
    void loginWithGoogle(GoogleSignInAccount googleSignInAccount);
    void saveUserData(FirebaseUser user);
}

