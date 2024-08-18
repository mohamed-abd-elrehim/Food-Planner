package com.example.mealmate.presenter.UserPreferencesPresenter;

import com.example.mealmate.model.userpreferencesmodel.user_preferences_model_interface.UserPreferencesModelInterface;
import com.example.mealmate.veiw.UserPreferencesViewInterface.UserPreferencesViewInterface;

public class UserPreferencesPresenter {

    private UserPreferencesModelInterface model;
    private UserPreferencesViewInterface view;

    public UserPreferencesPresenter(UserPreferencesModelInterface model, UserPreferencesViewInterface view) {
        this.model = model;
        this.view = view;
    }

    public void saveUserPreferences(String name, String email, String password, String id) {
        model.saveUserPreferences(name, email, password, id);
        view.onPreferencesSaved();
    }

    public void loadUserPreferences() {
        String name = model.getName();
        String email = model.getEmail();
        String password = model.getPassword();
        String id = model.getId();

        view.showName(name);
        view.showEmail(email);
        view.showPassword(password);
        view.showId(id);
    }

    public void clearUserPreferences() {
        model.clearPreferences();
        view.onPreferencesCleared();
    }

    public void updateFirebaseUserProfile(String name) {
        model.updateFirebaseUserProfile(name);
        view.onFirebaseUserUpdated();
    }
}
