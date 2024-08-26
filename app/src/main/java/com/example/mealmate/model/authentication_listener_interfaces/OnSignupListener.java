package com.example.mealmate.model.authentication_listener_interfaces;

import com.google.firebase.auth.FirebaseUser;

public interface OnSignupListener {
    void onSignupSuccess(FirebaseUser user);
    void onSignupFailure(String message);
}
