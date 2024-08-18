// UserPreferencesModel.java
package com.example.mealmate.model.userpreferencesmodel;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mealmate.model.userpreferencesmodel.user_preferences_model_interface.UserPreferencesModelInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserPreferencesModel implements UserPreferencesModelInterface {

    private static final String PREFS_NAME = "UserPrefs";
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;

    public UserPreferencesModel(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void saveUserPreferences(String name, String email, String password, String id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("password", password); // Storing passwords is not secure
        editor.putString("id", id);
        editor.apply();
        updateFirebaseUserProfile(name);
    }

    @Override
    public String getName() {
        return sharedPreferences.getString("name", null);
    }

    @Override
    public String getEmail() {
        return sharedPreferences.getString("email", null);
    }

    @Override
    public String getPassword() {
        return sharedPreferences.getString("password", null);
    }

    @Override
    public String getId() {
        return sharedPreferences.getString("id", null);
    }

    @Override
    public void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void updateFirebaseUserProfile(String name) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build());
        }
    }
}
