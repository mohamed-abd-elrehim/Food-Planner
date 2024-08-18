package com.example.mealmate.veiw.login_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.util.Patterns;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mealmate.R;
import com.example.mealmate.veiw.home_activity.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class LoginFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button login;
    private Button sign_in_with_google;
    private ImageView togglePasswordVisibility;
    private final String TAG = "LoginFragment";
    private boolean isPasswordVisible = false;
    private static final int RC_SIGN_IN = 9001;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))  // Replace with your OAuth 2.0 Client ID
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        sign_in_with_google = view.findViewById(R.id.continuewithgoogle);

        sign_in_with_google.setOnClickListener(v -> signInWithGoogle());

        login.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim().toLowerCase();
            String password = loginPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                // Proceed with login if validation passes
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {


                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                saveUserDetails(user);
                            }
                            Toast.makeText(getContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), HomeActivity.class));
                            requireActivity().finish(); // Finish the current activity if needed
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility());
    }


    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty() || !isValidPassword(password)) {
            Toast.makeText(getContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.visiblepass);  // Set the icon for hidden password
        } else {
            loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.hidepass);  // Set the icon for visible password
        }
        isPasswordVisible = !isPasswordVisible;
        loginPassword.setSelection(loginPassword.getText().length());  // Move cursor to the end
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // Google Sign-In was successful, now authenticate with Firebase
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                // Google Sign-In failed
                Toast.makeText(getContext(), "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {

                        String name = firebaseAuth.getCurrentUser().getDisplayName();
                        Map<String, Object> userMap = new HashMap<>();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        userMap.put("name", name);
                        String id = firebaseAuth.getCurrentUser().getUid();
                        db.collection("users").document(id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + id);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });


                        // Sign-in successful
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            saveUserDetails(user);
                        }
                        Toast.makeText(getContext(), "Sign-In successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), HomeActivity.class));
                        requireActivity().finish(); // Finish the current activity if needed
                    } else {
                        // Sign-in failed
                        Toast.makeText(getContext(), "Sign-In failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserDetails(FirebaseUser user) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", user.getUid());
        editor.putString("user_name", user.getDisplayName());
        editor.putString("user_email", user.getEmail());
        editor.putString("user_photo_url", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        editor.apply();
    }
}
