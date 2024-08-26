package com.example.mealmate.model.authentication_listener_interfaces;

import com.google.firebase.auth.FirebaseUser;

public interface OnLoginListener {
    void onLoginSuccess(FirebaseUser user);
    void onLoginFailure(String message);
}