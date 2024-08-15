package com.example.mealmate;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class StartFragment extends Fragment {
    private Button signupWithEmail;
    private Button continueWithGoogle;
    private TextView login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        ImageView imageView = view.findViewById(R.id.blurredImageView);
        Glide.with(this)
                .load(R.drawable.startscreenbacground)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 1)))
                .into(imageView);

        signupWithEmail = view.findViewById(R.id.signupwithemailBTU);
        continueWithGoogle = view.findViewById(R.id.continuewithgoogle);
        login = view.findViewById(R.id.alreadyhaveanaccountloginin);

        // Apply custom underline to a part of the text and make it clickable
        String fullText = getString(R.string.alreadyhaveanaccountloginin);
        /*

        SpannableString is a class in Android that allows you to
        apply different styles or behaviors to specific parts
        of a text string. It extends the Spannable interface, which
         provides methods to attach and manage various types of
         spans (e.g., formatting, clickable actions) within a text.
         */

        SpannableString spannableString = new SpannableString(fullText);
        int start = fullText.indexOf("Login In");
        int end = start + "Login In".length();

        // Apply custom underline
        spannableString.setSpan(new CustomUnderlineSpan(Color.RED, 3f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply text color
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the text clickable
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_startFragment_to_loginFragment);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // Remove default underline
                ds.setColor(Color.rgb(128, 0, 0)); // Ensure text color
            }

        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        login.setText(spannableString);
        login.setMovementMethod(LinkMovementMethod.getInstance()); // Make the text clickable

        signupWithEmail.setOnClickListener(V -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_startFragment_to_signUpFragment);
        });

        continueWithGoogle.setOnClickListener(V -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
        });
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
