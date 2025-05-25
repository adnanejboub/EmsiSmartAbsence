package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RattrapagesActivity extends AppCompatActivity {
    private static final String TAG = "RattrapagesActivity";
    private RecyclerView recyclerView;
    private RattrapageAdapter adapter;
    private List<RattrapageItem> rattrapageList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Spinner levelSpinner;
    private static final List<String> VALID_SITES = Arrays.asList(
            "Centre_1", "Centre_2", "Moulay_Youssef", "Berrinzrane", "Roudani", "Les_Orangers"
    );
    private static final List<String> VALID_LEVELS = Arrays.asList(
            "1AP", "2AP", "3IIR", "4IIR", "5", "IFA"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rattrapages);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialisé : " + (db != null));

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(RattrapagesActivity.this, Home.class);
            startActivity(intent);
            finish();
        });

        // Initialize views
        recyclerView = findViewById(R.id.rattrapages_recycler_view);
        levelSpinner = findViewById(R.id.spinner_level);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rattrapageList = new ArrayList<>();
        adapter = new RattrapageAdapter(rattrapageList);
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
                loadRattrapages(selectedLevel.equals("Tous") ? null : selectedLevel);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Insert test data if collection is empty, then load rattrapages
        insertTestData();
    }

    private void insertTestData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Utilisateur non connecté pour insérer les données de test");
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String userId = currentUser.getUid();

        // Vérifier si la collection est vide
        db.collection("rattrapages")
                .document(userId)
                .collection("rattrapage_entries")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "Collection vide, insertion des données de test");
                        List<RattrapageItem> testData = Arrays.asList(
                                new RattrapageItem("Développement Mobile", "4IIR-A", "2025-06-01", "08:00", "10:00", "Salle A101", "Centre_1", "4IIR"),
                                new RattrapageItem("Bases de Données", "4IIR-B", "2025-06-02", "14:00", "16:00", "Salle B204", "Roudani", "4IIR"),
                                new RattrapageItem("Réseaux Informatiques", "3IIR-C", "2025-06-03", "09:00", "11:00", "Salle C301", "Centre_2", "3IIR"),
                                new RattrapageItem("Algorithmique", "1AP-A", "2025-06-04", "08:30", "10:30", "Salle B205", "Les_Orangers", "1AP"),
                                new RattrapageItem("Programmation Java", "4IIR-A", "2025-06-05", "10:00", "12:00", "Salle A103", "Centre_2", "4IIR"),
                                new RattrapageItem("Développement Web", "2AP-B", "2025-06-06", "15:00", "17:00", "Salle A102", "Berrinzrane", "2AP"),
                                new RattrapageItem("Projet de Fin d'Année", "IFA-A", "2025-06-07", "10:00", "12:00", "Salle D401", "Moulay_Youssef", "IFA"),
                                new RattrapageItem("Mathématiques", "1AP-B", "2025-06-08", "13:00", "15:00", "Salle B206", "Les_Orangers", "1AP"),
                                new RattrapageItem("Systèmes d'Exploitation", "3IIR-A", "2025-06-09", "11:00", "13:00", "Salle C302", "Centre_1", "3IIR"),
                                new RattrapageItem("Anglais Technique", "5-A", "2025-06-10", "09:30", "11:30", "Salle A104", "Roudani", "5")
                        );

                        // Insérer les données avec un batch
                        db.runBatch(batch -> {
                            for (int i = 0; i < testData.size(); i++) {
                                batch.set(
                                        db.collection("rattrapages")
                                                .document(userId)
                                                .collection("rattrapage_entries")
                                                .document("rattrapage" + (i + 1)),
                                        testData.get(i)
                                );
                            }
                        }).addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Données de test insérées avec succès");
                            loadRattrapages(null); // Charger les données après insertion
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Erreur lors de l'insertion des données de test: " + e.getMessage(), e);
                            Toast.makeText(this, "Erreur lors de l'insertion: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            loadRattrapages(null); // Charger quand même pour vérifier
                        });
                    } else {
                        Log.d(TAG, "Collection non vide, pas d'insertion");
                        loadRattrapages(null); // Charger les données existantes
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors de la vérification de la collection: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    loadRattrapages(null);
                });
    }

    private void loadRattrapages(String levelFilter) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Utilisateur non connecté pour charger les rattrapages");
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "Chargement des données pour l'utilisateur : " + currentUser.getUid());

        String userId = currentUser.getUid();
        Query query = db.collection("rattrapages")
                .document(userId)
                .collection("rattrapage_entries")
                .whereIn("siteId", VALID_SITES);

        if (levelFilter != null) {
            query = query.whereEqualTo("level", levelFilter);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    rattrapageList.clear();
                    Log.d(TAG, "Nombre de documents récupérés : " + queryDocumentSnapshots.size());
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        RattrapageItem item = document.toObject(RattrapageItem.class);
                        // Filtrer les rattrapages futurs
                        try {
                            LocalDate rattrapageDate = LocalDate.parse(item.getDate());
                            if (rattrapageDate.isAfter(LocalDate.now()) || rattrapageDate.isEqual(LocalDate.now())) {
                                rattrapageList.add(item);
                                Log.d(TAG, "Rattrapage chargé : " + document.getData().toString());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Erreur de parsing de la date: " + e.getMessage());
                        }
                    }
                    // Trier par date et heure
                    rattrapageList.sort((item1, item2) -> {
                        try {
                            LocalDate date1 = LocalDate.parse(item1.getDate());
                            LocalDate date2 = LocalDate.parse(item2.getDate());
                            int dateComparison = date1.compareTo(date2);
                            if (dateComparison != 0) {
                                return dateComparison;
                            }
                            LocalTime time1 = LocalTime.parse(item1.getStartTime());
                            LocalTime time2 = LocalTime.parse(item2.getStartTime());
                            return time1.compareTo(time2);
                        } catch (Exception e) {
                            Log.e(TAG, "Erreur de tri: " + e.getMessage());
                            return 0;
                        }
                    });
                    adapter.notifyDataSetChanged();
                    if (rattrapageList.isEmpty()) {
                        Log.d(TAG, "Aucun rattrapage trouvé");
                        Toast.makeText(this, "Aucun rattrapage à venir", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors du chargement des rattrapages: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur: Impossible de charger les rattrapages: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}