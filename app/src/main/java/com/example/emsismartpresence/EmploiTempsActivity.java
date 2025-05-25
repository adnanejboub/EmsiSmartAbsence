package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmploiTempsActivity extends AppCompatActivity {
    private static final String TAG = "EmploiTempsActivity";
    private RecyclerView recyclerView;
    private EmploiTempsAdapter adapter;
    private List<EmploiTempsItem> emploiTempsList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ImageButton backButton;
    private Spinner levelSpinner;
    private static final List<String> DAYS_ORDER = Arrays.asList(
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"
    );
    private static final List<String> VALID_SITES = Arrays.asList(
            "Centre_1", "Centre_2", "Moulay_Youssef", "Berrinzrane", "Roudani", "Les_Orangers"
    );
    private static final List<String> VALID_LEVELS = Arrays.asList(
            "1AP", "2AP", "3IIR", "4IIR", "5", "IFA"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emploi_temps);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialisé : " + (db != null));

        // Initialize views
        recyclerView = findViewById(R.id.emploi_temps_recycler_view);
        backButton = findViewById(R.id.back_button);
        levelSpinner = findViewById(R.id.spinner_level);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emploiTempsList = new ArrayList<>();
        adapter = new EmploiTempsAdapter(emploiTempsList);
        recyclerView.setAdapter(adapter);

        // Set up level spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>() {{ add("Tous"); addAll(VALID_LEVELS); }});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(spinnerAdapter);
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLevel = parent.getItemAtPosition(position).toString();
                loadEmploiTemps(selectedLevel.equals("Tous") ? null : selectedLevel);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set back button listener
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmploiTempsActivity.this, Home.class);
            startActivity(intent);
            finish();
        });

        // Initialize schedules for testing
        initializeTestSchedules();
    }

    private void initializeTestSchedules() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Utilisateur non connecté");
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String userId = currentUser.getUid();

        // Check if schedules have already been initialized
        db.collection("schedules")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getBoolean("initial_setup") == true) {
                        Log.d(TAG, "Schedules already initialized, loading existing schedules");
                        loadEmploiTemps(null);
                    } else {
                        // Delete existing schedules and create new ones
                        deleteAndCreateSchedules(userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors de la vérification de l'initialisation: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur lors de l'initialisation: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void deleteAndCreateSchedules(String userId) {
        // Delete all existing schedules
        db.collection("schedules")
                .document(userId)
                .collection("schedule_entries")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    Log.d(TAG, "Existing schedules deleted");
                    // Create new test schedules
                    createTestSchedules(userId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors de la suppression des schedules: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur lors de la suppression: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void createTestSchedules(String userId) {
        // Sample schedule entries (10 entries, Monday to Saturday)
        List<EmploiTempsItem> testSchedules = new ArrayList<>();
        testSchedules.add(new EmploiTempsItem("Mathématiques", "G1", "Lundi", "08:00", "10:00", "Salle A1", "Centre_1", "4IIR"));
        testSchedules.add(new EmploiTempsItem("Programmation Java", "G2", "Lundi", "10:30", "12:30", "Salle B2", "Moulay_Youssef", "3IIR"));
        testSchedules.add(new EmploiTempsItem("Base de Données", "G3", "Mardi", "09:00", "11:00", "Salle C3", "Roudani", "4IIR"));
        testSchedules.add(new EmploiTempsItem("Réseaux", "G4", "Mardi", "14:00", "16:00", "Salle D4", "Centre_2", "5"));
        testSchedules.add(new EmploiTempsItem("Algorithmique", "G5", "Mercredi", "08:30", "10:30", "Salle E5", "Les_Orangers", "2AP"));
        testSchedules.add(new EmploiTempsItem("Développement Mobile", "G6", "Mercredi", "11:00", "13:00", "Salle F6", "Berrinzrane", "4IIR"));
        testSchedules.add(new EmploiTempsItem("Systèmes d'Exploitation", "G7", "Jeudi", "10:00", "12:00", "Salle A7", "Centre_1", "3IIR"));
        testSchedules.add(new EmploiTempsItem("Projet de Fin d'Études", "G8", "Jeudi", "15:00", "17:00", "Salle B8", "Roudani", "5"));
        testSchedules.add(new EmploiTempsItem("Anglais Technique", "G9", "Vendredi", "09:30", "11:30", "Salle C9", "Moulay_Youssef", "IFA"));
        testSchedules.add(new EmploiTempsItem("Gestion de Projet", "G10", "Samedi", "08:00", "10:00", "Salle D10", "Centre_2", "4IIR"));

        // Save schedules to Firestore
        for (EmploiTempsItem item : testSchedules) {
            Map<String, Object> scheduleData = new HashMap<>();
            scheduleData.put("courseName", item.getCourseName());
            scheduleData.put("groupId", item.getGroupId());
            scheduleData.put("day", item.getDay());
            scheduleData.put("startTime", item.getStartTime());
            scheduleData.put("endTime", item.getEndTime());
            scheduleData.put("room", item.getRoom());
            scheduleData.put("siteId", item.getSiteId());
            scheduleData.put("level", item.getLevel());

            db.collection("schedules")
                    .document(userId)
                    .collection("schedule_entries")
                    .document() // Auto-generate document ID
                    .set(scheduleData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Schedule entry added: " + item.getCourseName()))
                    .addOnFailureListener(e -> Log.e(TAG, "Erreur lors de l'ajout de l'entrée: " + e.getMessage(), e));
        }

        // Mark schedules as initialized
        db.collection("schedules")
                .document(userId)
                .set(new HashMap<String, Object>() {{ put("initial_setup", true); }})
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Schedules initialized and marked as setup");
                    loadEmploiTemps(null); // Load the newly created schedules
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors du marquage de l'initialisation: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur lors de l'initialisation: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadEmploiTemps(String levelFilter) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Utilisateur non connecté pour charger l'emploi du temps");
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "Chargement des données pour l'utilisateur : " + currentUser.getUid());

        String userId = currentUser.getUid();
        Query query = db.collection("schedules")
                .document(userId)
                .collection("schedule_entries")
                .whereIn("siteId", VALID_SITES);

        if (levelFilter != null) {
            query = query.whereEqualTo("level", levelFilter);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    emploiTempsList.clear();
                    Log.d(TAG, "Nombre de documents récupérés : " + queryDocumentSnapshots.size());
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        EmploiTempsItem item = document.toObject(EmploiTempsItem.class);
                        emploiTempsList.add(item);
                        Log.d(TAG, "Document chargé : " + document.getData().toString());
                    }
                    // Sort the list by day and startTime
                    emploiTempsList.sort((item1, item2) -> {
                        int dayIndex1 = DAYS_ORDER.indexOf(item1.getDay());
                        int dayIndex2 = DAYS_ORDER.indexOf(item2.getDay());
                        if (dayIndex1 != dayIndex2) {
                            return Integer.compare(dayIndex1, dayIndex2);
                        }
                        try {
                            LocalTime time1 = LocalTime.parse(item1.getStartTime());
                            LocalTime time2 = LocalTime.parse(item2.getStartTime());
                            return time1.compareTo(time2);
                        } catch (Exception e) {
                            Log.e(TAG, "Erreur de parsing des heures: " + e.getMessage());
                            return 0;
                        }
                    });
                    adapter.notifyDataSetChanged();
                    if (emploiTempsList.isEmpty()) {
                        Log.d(TAG, "Aucun emploi du temps trouvé");
                        Toast.makeText(this, "Aucun emploi du temps trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors du chargement de l'emploi du temps: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur: Impossible de charger l'emploi du temps: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}