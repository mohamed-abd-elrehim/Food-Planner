package com.example.mealmate.veiw.login_fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mealmate.R;
import com.example.mealmate.presenter.login_fragment_presenter.LoginFragmentPresenter;
import com.example.mealmate.veiw.home_activity.HomeActivity;
import com.example.mealmate.veiw.login_fragment.login_fragment_view_Interface.LoginFragmentViewInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class LoginFragment extends Fragment implements LoginFragmentViewInterface {
    private static final String TAG = "LoginFragment";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button login;
    private Button signInWithGoogle;
    private ImageView togglePasswordVisibility;
    private ProgressBar progressBar;
    private boolean isPasswordVisible = false;
    private LoginFragmentPresenter presenter;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    );
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        presenter = new LoginFragmentPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.blurredImageLoginView);
        Glide.with(this)
                .load(R.drawable.logninscreenbackground)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 1)))
                .into(imageView);

        loginEmail = view.findViewById(R.id.email_input_login);
        loginPassword = view.findViewById(R.id.password_input_login);
        login = view.findViewById(R.id.login_button);
        togglePasswordVisibility = view.findViewById(R.id.toggle_password_visibility);
        signInWithGoogle = view.findViewById(R.id.continuewithgoogle);
        progressBar = view.findViewById(R.id.progressBarLogin);

        signInWithGoogle.setOnClickListener(v -> signInWithGoogle());

        login.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim().toLowerCase();
            String password = loginPassword.getText().toString().trim();
            if (validateInputs(email, password)) {
                showLoading();
                presenter.loginWithEmailAndPassword(email, password);
            }
        });

        togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.visiblepass);
        } else {
            loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.hidepass);
        }
        isPasswordVisible = !isPasswordVisible;
        loginPassword.setSelection(loginPassword.getText().length());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            showLoading();

                            presenter.loginWithGoogle(account);

                        }
                    } catch (ApiException e) {
                        Toast.makeText(getContext(), getString(R.string.google_sign_in_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.setProgress(100, true);
    }

    @Override
    public void hideLoading()
    {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoginSuccess(String message) {
        hideLoading();
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        startHomeActivity();
    }

    @Override
    public void onLoginFailure(String message) {
        hideLoading();
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserDataSaveFailure(String errorMessage) {
        Log.w(TAG, "Error saving user data: " + errorMessage);
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void saveUserDetails(FirebaseUser user) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", user.getUid());
        editor.putString("user_name", user.getDisplayName());
        editor.putString("user_email", user.getEmail());
        editor.putString("user_photo_url", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        editor.apply();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onLoginFailure(getString(R.string.please_enter_a_valid_email));
            return false;
        }

        if (password.isEmpty() || !isValidPassword(password)) {
            onLoginFailure(getString(R.string.please_enter_a_valid_password));
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}