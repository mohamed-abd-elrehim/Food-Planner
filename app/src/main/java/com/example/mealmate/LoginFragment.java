package com.example.mealmate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
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
import com.example.mealmate.veiw.HomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class LoginFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button login;
    private ImageView togglePasswordVisibility;

    private boolean isPasswordVisible = false;
    // Regex pattern for validating the password
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        firebaseAuth = FirebaseAuth.getInstance();
        loginEmail = view.findViewById(R.id.email_input_login);
        loginPassword = view.findViewById(R.id.password_input_login);
        login = view.findViewById(R.id.login_button);
        togglePasswordVisibility = view.findViewById(R.id.toggle_password_visibility);

        login.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim().toLowerCase();
            String password = loginPassword.getText().toString().trim();


            if (validateInputs(email, password)) {
                // Proceed with login if validation passes
                firebaseAuth.signInWithEmailAndPassword(email.toLowerCase(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), HomeActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
        String passwordPattern =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
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


}
