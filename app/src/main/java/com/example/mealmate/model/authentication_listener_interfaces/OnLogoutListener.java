package com.example.mealmate.model.authentication_listener_interfaces;
public interface OnLogoutListener {
    void onLogoutSuccess();
    void onLogoutFailure(String message);
}
