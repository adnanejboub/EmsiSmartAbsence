package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        TextView currentUserEmail = navigationView.getHeaderView(0).findViewById(R.id.currentU);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            currentUserEmail.setText(currentUser.getEmail());
        } else {
            currentUserEmail.setText("Non connecté");
            Log.w(TAG, "Aucun utilisateur connecté ou email non disponible");
        }


        ImageView menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));


        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            try {
                if (itemId == R.id.nav_maps) {
                    Intent intent = new Intent(Home.this, MapsActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.nav_documents) {
                    Toast.makeText(Home.this, "Fonctionnalité Documents à venir", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_absence) {
                    Toast.makeText(Home.this, "Fonctionnalité Absence à venir", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_assistant) {
                    Intent intent = new Intent(Home.this, Assistant_virtuel.class);
                    startActivity(intent);
                } else if (itemId == R.id.nav_rattrapage) {
                    Toast.makeText(Home.this, "Fonctionnalité Rattrapage à venir", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_emploi) {
                    Toast.makeText(Home.this, "Fonctionnalité Emploi du temps à venir", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_profile) {
                    Toast.makeText(Home.this, "Fonctionnalité Profil à venir", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_logout) {
                    mAuth.signOut();
                    Intent intent = new Intent(Home.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers " + item.getTitle(), e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir " + item.getTitle(), Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Card 1: Maps
        CardView card1 = findViewById(R.id.card1);
        card1.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Home.this, MapsActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers MapsActivity", e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir Maps", Toast.LENGTH_SHORT).show();
            }
        });

        // Card 2: Documents
        CardView card2 = findViewById(R.id.card2);
        card2.setOnClickListener(v -> Toast.makeText(Home.this, "Fonctionnalité Documents à venir", Toast.LENGTH_SHORT).show());

        // Card 3: Absence
        CardView card3 = findViewById(R.id.card3);
        card3.setOnClickListener(v -> Toast.makeText(Home.this, "Fonctionnalité Absence à venir", Toast.LENGTH_SHORT).show());

        // Card 4: Assistant AI
        CardView card4 = findViewById(R.id.card4);
        card4.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Home.this, Assistant_virtuel.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers Assistant_virtuel", e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir Assistant AI", Toast.LENGTH_SHORT).show();
            }
        });

        // Card 5: Rattrapage
        CardView card5 = findViewById(R.id.card5);
        card5.setOnClickListener(v -> Toast.makeText(Home.this, "Fonctionnalité Rattrapage à venir", Toast.LENGTH_SHORT).show());

        // Card 6: Emploi du temps
        CardView card6 = findViewById(R.id.card6);
        card6.setOnClickListener(v -> Toast.makeText(Home.this, "Fonctionnalité Emploi du temps à venir", Toast.LENGTH_SHORT).show());

        // Card 7: Profile
        CardView card7 = findViewById(R.id.card7);
        card7.setOnClickListener(v -> Toast.makeText(Home.this, "Fonctionnalité Profil à venir", Toast.LENGTH_SHORT).show());
    }
}