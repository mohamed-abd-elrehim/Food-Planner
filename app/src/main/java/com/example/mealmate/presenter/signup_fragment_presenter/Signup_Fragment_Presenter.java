package com.example.mealmate.presenter.signup_fragment_presenter;


import com.example.mealmate.R;
import com.example.mealmate.presenter.signup_fragment_presenter.signup_presenter_interface.Signup_Presenter_Interface;
import com.example.mealmate.veiw.signup_fragment.signup_fragment_view_interface.SignUpFragmentViewInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup_Fragment_Presenter implements Signup_Presenter_Interface {

    private  SignUpFragmentViewInterface view;
    private  FirebaseAuth firebaseAuth;
    private  FirebaseFirestore db;

    public Signup_Fragment_Presenter(SignUpFragmentViewInterface view) {
        this.view = view;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void signUp(String name, String email, String password, String confirmPassword) {
        if (validateInputs(email, password, confirmPassword)) {
            view.showLoading();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            saveUserData(name);
                        } else {
                            view.onSignupFailure(R.string.sign_up_failed + task.getException().getMessage());
                            view.hideLoading();
                        }
                    });
        }
    }

    private void saveUserData(String name) {
        String id = firebaseAuth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);

        db.collection("users").document(id).set(user)
                .addOnSuccessListener(aVoid -> {
                    view.onSignUpSuccess(String.valueOf(R.string.sign_in_successful));
                    view.hideLoading();
                })
                .addOnFailureListener(e -> {
                    view.onSignupFailure(String.valueOf(R.string.sign_up_failed) + e.getMessage());
                    view.hideLoading();
                });
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.onSignupFailure(String.valueOf(R.string.please_enter_a_valid_email));
            return false;
        }
        if (password.isEmpty() || !isValidPassword(password)) {
            view.onSignupFailure(String.valueOf(R.string.please_enter_a_valid_password));
            return false;
        }
        if (!password.equals(confirmPassword)) {
            view.onSignupFailure(String.valueOf(R.string.passwords_do_not_match));
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }
}