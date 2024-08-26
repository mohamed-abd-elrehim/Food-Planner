package com.example.mealmate.presenter.login_fragment_presenter;

import android.util.Patterns;

import com.example.mealmate.R;
import com.example.mealmate.presenter.login_fragment_presenter.login_fragment_interface.LoginFragmentPresenterInterface;
import com.example.mealmate.veiw.login_fragment.login_fragment_view_Interface.LoginFragmentViewInterface;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
public class LoginFragmentPresenter implements LoginFragmentPresenterInterface {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    );
    private static final String TAG = "LoginFragmentPresenter";

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;
    private final LoginFragmentViewInterface view;

    public LoginFragmentPresenter(LoginFragmentViewInterface view) {
        this.view = view;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void loginWithEmailAndPassword(String email, String password) {
        if (validateInputs(email, password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            view.saveUserDetails(user);
                            view.onLoginSuccess(String.valueOf(R.string.login_successfully));
                        }
                    })
                    .addOnFailureListener(e -> view.onLoginFailure(String.valueOf(R.string.login_failed)));
        }
    }

    @Override
    public void loginWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        saveUserData(user);
                    }
                })
                .addOnFailureListener(e -> view.onLoginFailure(String.valueOf(R.string.login_failed)));
    }

    @Override
    public void saveUserData(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("name", user.getDisplayName());
        userData.put("email", user.getEmail());
        userData.put("photo_url", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> view.onLoginSuccess(String.valueOf(R.string.user_data_saved_successfully)))
                .addOnFailureListener(e -> view.onUserDataSaveFailure(String.valueOf(R.string.user_data_save_failed) + e.getMessage()));
    }

    private boolean validateInputs(String email, String password) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && !password.isEmpty() && PASSWORD_PATTERN.matcher(password).matches();
    }
}