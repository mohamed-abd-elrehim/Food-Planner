package com.example.mealmate.veiw.home_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mealmate.R;
import com.example.mealmate.veiw.main_activity.MainActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up the navigation drawer header
        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.profile_name);
        TextView email = headerView.findViewById(R.id.profile_email);

        // Fetch user data from Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    name.setText(document.getString("name"));
                    email.setText(auth.getCurrentUser().getEmail());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // Handle menu item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                auth.signOut();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("destination_fragment", "loginFragment");
                startActivity(intent);

            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set up the toggle button for opening and closing the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
