package com.example.mealmate.veiw.start_fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

import jp.wasabeef.glide.transformations.BlurTransformation;

public class StartFragment extends Fragment {
    private Button signupWithEmail;
    private Button continueWithGoogle;
    private TextView login;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient signInClient;
    private static final int RC_SIGN_IN = 9001;
    private final String TAG = "StartFragment";
    private Button gestMode ;

    // Access UI elements in the custom layout
    TextView title ;
    TextView message;
    Button goButton ;
    Button cancelButton ;
    AlertDialog dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance of AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        // Set the custom layout to the dialog
        builder.setView(dialogView);
        // Create and show the dialog
        dialog = builder.create();

        title = dialogView.findViewById(R.id.custom_title);
        message = dialogView.findViewById(R.id.custom_message);
        goButton = dialogView.findViewById(R.id.button_go);
        cancelButton = dialogView.findViewById(R.id.button_cancel);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))  // Ensure client_id matches OAuth 2.0 client ID
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(requireContext(), gso);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize the Button using the view from onCreateView
        gestMode = view.findViewById(R.id.skipButton);

        // Check if gestMode is not null
        if (gestMode != null) {

            gestMode.setOnClickListener(v -> {
                title.setText(R.string.wait_are_you_sure);
                message.setText(R.string.you_ll_miss_out_on_personalized_content_and_saving_our_delicious_recipes);
                goButton.setText(R.string.i_m_sure);
                cancelButton.setText(R.string.no_go_back);

                goButton.setOnClickListener(vi -> {
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    intent.putExtra("user_type", "guest");
                    startActivity(intent);
                    requireActivity().finish();
                });
                cancelButton.setOnClickListener(vi -> {
                    dialog.dismiss();
                });
                dialog.show();

            });
        } else {
            Log.e("StartFragment", "Button with ID skipButton not found.");
        }





        ImageView imageView = view.findViewById(R.id.blurredImageView);
        Glide.with(this)
                .load(R.drawable.startscreenbacground)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 1)))
                .into(imageView);

        signupWithEmail = view.findViewById(R.id.signupwithemailBTU);
        continueWithGoogle = view.findViewById(R.id.continuewithgoogle);
        login = view.findViewById(R.id.alreadyhaveanaccountloginin);

        // Apply custom underline and clickable span
        applyClickableText();

        signupWithEmail.setOnClickListener(v -> {
            navigateToSignUPFragment();
        });

        continueWithGoogle.setOnClickListener(v -> {
            signInWithGoogle();
        });
    }

    private void applyClickableText() {
        String fullText = getString(R.string.alreadyhaveanaccountloginin);
        SpannableString spannableString = new SpannableString(fullText);
        int start = fullText.indexOf("Login");
        int end = start + "Login".length();

        // Apply custom underline
        spannableString.setSpan(new CustomUnderlineSpan(Color.RED, 3f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply text color
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the text clickable
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                navigateToLoginFragment();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // Remove default underline
                ds.setColor(Color.rgb(128, 0, 0)); // Set text color
            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        login.setText(spannableString);
        login.setMovementMethod(LinkMovementMethod.getInstance()); // Make the text clickable
    }

    private void navigateToSignUPFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_startFragment_to_signUpFragment);
    }
    private void navigateToLoginFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_startFragment_to_loginFragment);
    }

    private void signInWithGoogle() {
        Intent signInIntent = signInClient.getSignInIntent();
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
                Toast.makeText(getContext(), R.string.google_sign_in_failed + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        String name =  firebaseAuth.getCurrentUser().getDisplayName();
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
                            }});

                        // Sign-in successful
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Save user details
                            saveUserDetails(user);
                        }
                        Toast.makeText(getContext(), "Sign-In successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), HomeActivity.class));
                        requireActivity().finish(); // Finish the current activity if needed
                    } else {
                        // Sign-in failed
                        Toast.makeText(getContext(),  R.string.sign_in_failed + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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

    public class CustomUnderlineSpan extends UnderlineSpan {
        private final Paint mPaint;

        public CustomUnderlineSpan(int color, float thickness) {
            mPaint = new Paint();
            mPaint.setColor(color);
            mPaint.setStrokeWidth(thickness);
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            super.updateDrawState(paint);
            paint.setUnderlineText(false); // Disable default underline
            paint.setColor(mPaint.getColor());
            paint.setStrokeWidth(mPaint.getStrokeWidth());
        }
    }
}