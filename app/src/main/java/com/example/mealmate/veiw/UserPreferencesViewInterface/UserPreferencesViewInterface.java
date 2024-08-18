package com.example.mealmate.veiw.UserPreferencesViewInterface;
// UserPreferencesViewInterface.java

public interface UserPreferencesViewInterface {
    void showName(String name);
    void showEmail(String email);
    void showPassword(String password);
    void showId(String id);
    void onPreferencesSaved();
    void onPreferencesCleared();
    void onFirebaseUserUpdated();
}
