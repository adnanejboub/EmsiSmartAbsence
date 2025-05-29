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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Home extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView coursAujourdhui, rattrapagesCount, prochainCours, welcomeText;
    private static final List<String> DAYS_ORDER = Arrays.asList(
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up navigation drawer header
        TextView currentUserEmail = navigationView.getHeaderView(0).findViewById(R.id.currentU);
        welcomeText = findViewById(R.id.myDashboard);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            currentUserEmail.setText(currentUser.getEmail());
            updateUserName(currentUser.getUid());
        } else {
            currentUserEmail.setText("Non connecté");
            welcomeText.setText("Bienvenue");
            Log.w(TAG, "Aucun utilisateur connecté ou email non disponible");
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        ImageView menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            try {
                if (itemId == R.id.nav_maps) {
                    startActivity(new Intent(Home.this, MapsActivity.class));
                } else if (itemId == R.id.nav_documents) {
                    startActivity(new Intent(Home.this, DocumentsActivity.class));
                } else if (itemId == R.id.nav_absence) {
                    startActivity(new Intent(Home.this, AbsenceMarkingActivity.class));
                } else if (itemId == R.id.nav_assistant) {
                    startActivity(new Intent(Home.this, Assistant_virtuel.class));
                } else if (itemId == R.id.nav_rattrapage) {
                    startActivity(new Intent(Home.this, RattrapagesActivity.class));
                } else if (itemId == R.id.nav_emploi) {
                    startActivity(new Intent(Home.this, EmploiTempsActivity.class));
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(Home.this, Profil.class));
                } else if (itemId == R.id.nav_logout) {
                    mAuth.signOut();
                    startActivity(new Intent(Home.this, Login.class));
                    finish();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers " + item.getTitle(), e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir " + item.getTitle(), Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Initialize statistic views
        coursAujourdhui = findViewById(R.id.cours_aujourdhui);
        rattrapagesCount = findViewById(R.id.rattrapages_count);
        prochainCours = findViewById(R.id.prochain_cours);

        // Load data
        loadTodayCourses();
        loadRattrapages();
        loadNextCourse();

        // Card 1: Maps
        CardView card1 = findViewById(R.id.card1);
        card1.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Home.this, MapsActivity.class));
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers MapsActivity", e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir Maps", Toast.LENGTH_SHORT).show();
            }
        });

        // Card 2: Documents
        CardView card2 = findViewById(R.id.card2);
        card2.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Home.this, DocumentsActivity.class));
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers DocumentActivity", e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir Documents", Toast.LENGTH_SHORT).show();
            }
        });

        // Card 3: Absence
        CardView card3 = findViewById(R.id.card3);
        card3.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Home.this, AbsenceMarkingActivity.class));
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers AbsenceMarkingActivity", e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir Absence", Toast.LENGTH_SHORT).show();
            }
        });

        // Card 4: Assistant AI
        CardView card4 = findViewById(R.id.card4);
        card4.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Home.this, Assistant_virtuel.class));
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers Assistant_virtuel", e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir Assistant AI", Toast.LENGTH_SHORT).show();
            }
        });

        // Card 5: Rattrapage
        CardView card5 = findViewById(R.id.card5);
        card5.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Home.this, RattrapagesActivity.class));
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers RattrapagesActivity", e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir Rattrapages", Toast.LENGTH_SHORT).show();
            }
        });

        // Card 6: Emploi du temps
        CardView card6 = findViewById(R.id.card6);
        card6.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Home.this, EmploiTempsActivity.class));
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers EmploiTempsActivity", e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir Emploi du temps", Toast.LENGTH_SHORT).show();
            }
        });

        // Card 7: Profile
        CardView card7 = findViewById(R.id.card7);
        card7.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Home.this, Profil.class));
            } catch (Exception e) {
                Log.e(TAG, "Erreur de navigation vers Profil", e);
                Toast.makeText(Home.this, "Erreur: Impossible d'ouvrir Profil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserName(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        if (fullName != null && !fullName.trim().isEmpty()) {
                            welcomeText.setText("Bienvenue " + fullName);
                            Log.d(TAG, "Nom de l'utilisateur mis à jour: " + fullName);
                        } else {
                            welcomeText.setText("Bienvenue");
                            Log.w(TAG, "Champ fullName vide ou null pour userId: " + userId);
                        }
                    } else {
                        welcomeText.setText("Bienvenue");
                        Log.w(TAG, "Document utilisateur non trouvé pour userId: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors du chargement du nom de l'utilisateur", e);
                    welcomeText.setText("Bienvenue");
                    Toast.makeText(Home.this, "Erreur lors du chargement du nom", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadTodayCourses() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Aucun utilisateur connecté");
            coursAujourdhui.setText("Erreur");
            return;
        }

        String userId = currentUser.getUid();
        LocalDate today = LocalDate.now();
        String dayName = getCurrentDayName(today);

        db.collection("schedules")
                .document(userId)
                .collection("schedule_entries")
                .whereEqualTo("day", dayName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    coursAujourdhui.setText(String.valueOf(count));
                    Log.d(TAG, "Nombre de cours aujourd'hui pour " + dayName + ": " + count);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors du chargement des cours: " + e.getMessage(), e);
                    coursAujourdhui.setText("Erreur");
                });
    }

    private void loadRattrapages() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Aucun utilisateur connecté");
            rattrapagesCount.setText("Erreur");
            return;
        }

        String userId = currentUser.getUid();
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE);

        db.collection("rattrapages")
                .document(userId)
                .collection("rattrapage_entries")
                .whereGreaterThanOrEqualTo("date", todayStr)
                .get()
                .addOnSuccessListener(query -> {
                    int count = query.size();
                    rattrapagesCount.setText(String.valueOf(count));
                    Log.d(TAG, "Nombre de rattrapages à venir: " + count);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors du chargement des rattrapages: " + e.getMessage(), e);
                    rattrapagesCount.setText("Erreur");
                });
    }

    private void loadNextCourse() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Aucun utilisateur connecté");
            prochainCours.setText("Erreur");
            return;
        }

        String userId = currentUser.getUid();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<EmploiTempsItem> courses = new ArrayList<>();

        db.collection("schedules")
                .document(userId)
                .collection("schedule_entries")
                .orderBy("day", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        EmploiTempsItem item = document.toObject(EmploiTempsItem.class);
                        courses.add(item);
                    }
                    EmploiTempsItem nextCourse = findNextCourse(courses, today, now);
                    if (nextCourse != null) {
                        String nextCourseText = String.format("%s à %s (%s)",
                                nextCourse.getCourseName(),
                                nextCourse.getStartTime(),
                                nextCourse.getDay());
                        prochainCours.setText(nextCourseText);
                        Log.d(TAG, "Prochain cours: " + nextCourseText);
                    } else {
                        prochainCours.setText("Aucun");
                        Log.d(TAG, "Aucun prochain cours trouvé");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors du chargement du prochain cours: " + e.getMessage(), e);
                    prochainCours.setText("Erreur");
                });
    }

    private String getCurrentDayName(LocalDate date) {
        String dayName = date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH);
        return Character.toUpperCase(dayName.charAt(0)) + dayName.substring(1);
    }

    private EmploiTempsItem findNextCourse(List<EmploiTempsItem> courses, LocalDate today, LocalTime now) {
        EmploiTempsItem next = null;
        LocalTime earliest = LocalTime.MAX;
        LocalDate nextDate = today;
        int daysAhead = 0;

        while (daysAhead < 7 && next == null) {
            String dayName = getCurrentDayName(nextDate);
            if (!DAYS_ORDER.contains(dayName)) {
                nextDate = nextDate.plusDays(1);
                daysAhead++;
                continue;
            }

            for (EmploiTempsItem course : courses) {
                if (course.getDay().equals(dayName)) {
                    try {
                        LocalTime startTime = LocalTime.parse(course.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
                        if (nextDate.equals(today)) {
                            if (startTime.isAfter(now) && startTime.isBefore(earliest)) {
                                earliest = startTime;
                                next = course;
                            }
                        } else {
                            if (startTime.isBefore(earliest)) {
                                earliest = startTime;
                                next = course;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erreur lors du parsing de l'heure pour le cours: " + course.getCourseName(), e);
                    }
                }
            }
            nextDate = nextDate.plusDays(1);
            daysAhead++;
        }
        return next;
    }
}