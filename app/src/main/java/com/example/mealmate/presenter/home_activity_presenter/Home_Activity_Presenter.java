package com.example.mealmate.presenter.home_activity_presenter;

import android.util.Log;

import com.example.mealmate.presenter.home_activity_presenter.home_activity_presenter_interface.Home_Activity_Presenter_Interface;
import com.example.mealmate.veiw.home_activity.home_activity_interface.Home_Activity_Interface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Home_Activity_Presenter implements Home_Activity_Presenter_Interface {
    private Home_Activity_Interface view;
    private static final String TAG = "HomeActivity_presenter";
    private FirebaseAuth firebaseAuth;
    public Home_Activity_Presenter (Home_Activity_Interface view)
    {
        this.view=view;
    }
    @Override
    public void getUserData() {

        // Fetch user data from Firebase
         firebaseAuth = FirebaseAuth.getInstance();
        Log.i(TAG, "onCreate: " + firebaseAuth.getCurrentUser().getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        Log.i(TAG, "onCreate: " + document.getString("name")+firebaseAuth.getCurrentUser().getEmail());
                        view.updateProfile(document.getString("name").toString(),firebaseAuth.getCurrentUser().getEmail());

                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
    }
}
