package com.example.mealmate.model.userpreferencesmodel.user_preferences_model_interface;

public interface UserPreferencesModelInterface {
    void saveUserPreferences(String name, String email, String password, String id);
    String getName();
    String getEmail();
    String getPassword();
    String getId();
    void clearPreferences();
    void updateFirebaseUserProfile(String name);
}
