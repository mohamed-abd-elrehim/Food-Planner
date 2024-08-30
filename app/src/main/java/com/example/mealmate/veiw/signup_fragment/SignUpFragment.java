package com.example.mealmate.veiw.signup_fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.util.Log;
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
import com.example.mealmate.presenter.signup_fragment_presenter.Signup_Fragment_Presenter;
import com.example.mealmate.veiw.signup_fragment.signup_fragment_view_interface.SignUpFragmentViewInterface;

import jp.wasabeef.glide.transformations.BlurTransformation;
import android.text.InputType;
public class SignUpFragment extends Fragment implements SignUpFragmentViewInterface {

    private Signup_Fragment_Presenter presenter;
    private EditText sginupName;
    private EditText sginupEmail;
    private EditText sginupPassword;
    private EditText sginupconfirmPassword;
    private Button sginup;
    private ImageView togglePasswordVisibility;
    private ImageView toggleConfirmPasswordVisibility;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private final String TAG = "SignUpFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new Signup_Fragment_Presenter(this);

        ImageView imageView = view.findViewById(R.id.blurredImagesignupView);
        Glide.with(this)
                .load(R.drawable.signupscreenbackground)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 1)))
                .into(imageView);

        sginupEmail = view.findViewById(R.id.email_input);
        sginupPassword = view.findViewById(R.id.password_input);
        sginupconfirmPassword = view.findViewById(R.id.confirm_password_input);
        sginup = view.findViewById(R.id.signup_button);
        sginupName = view.findViewById(R.id.name_input);
        togglePasswordVisibility = view.findViewById(R.id.toggle_password_visibility);
        toggleConfirmPasswordVisibility = view.findViewById(R.id.toggle_confirmpassword_visibility);

        sginup.setOnClickListener(v -> {
            String name = sginupName.getText().toString().trim();
            String email = sginupEmail.getText().toString().trim().toLowerCase();
            String password = sginupPassword.getText().toString().trim();
            String confirmPassword = sginupconfirmPassword.getText().toString().trim();

            presenter.signUp(name, email, password, confirmPassword);
        });

        togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility());
        toggleConfirmPasswordVisibility.setOnClickListener(v -> toggleConfirmPasswordVisibility());
    }

    @Override
    public void showLoading() {
        // Show a loading spinner or progress indicator
    }

    @Override
    public void hideLoading() {
                // Hide the loading spinner or progress indicator
    }

    @Override
    public void onSignUpSuccess(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_signUpFragment_to_loginFragment);
    }



    @Override
    public void onSignupFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }



    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            sginupPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.visiblepass);
        } else {
            sginupPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.hidepass);
        }
        isPasswordVisible = !isPasswordVisible;
        sginupPassword.setSelection(sginupPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            sginupconfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleConfirmPasswordVisibility.setImageResource(R.drawable.visiblepass);
        } else {
            sginupconfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleConfirmPasswordVisibility.setImageResource(R.drawable.hidepass);
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        sginupconfirmPassword.setSelection(sginupconfirmPassword.getText().length());
    }
}