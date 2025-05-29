package com.example.emsismartpresence;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DocumentAdapter documentAdapter;
    private List<Document> documentList;
    private List<Document> fullDocumentList; // Liste complète pour le filtrage
    private FirebaseFirestore db;
    private Spinner filterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        // Configurer la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Documents");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Gérer le clic sur le bouton de retour
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();

        // Initialiser RecyclerView
        recyclerView = findViewById(R.id.recycler_view_documents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentList = new ArrayList<>();
        fullDocumentList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentList);
        recyclerView.setAdapter(documentAdapter);

        // Configurer le Spinner
        filterSpinner = findViewById(R.id.filter_spinner);
        String[] filterOptions = {"Tous", "Cours", "TD", "TP"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        // Gérer la sélection du filtre
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = filterOptions[position];
                filterDocuments(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ne rien faire
            }
        });

        // Vérifier et créer les documents
        checkAndCreateDocuments();
    }

    private void checkAndCreateDocuments() {
        db.collection("documents").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int existingDocuments = task.getResult().size();
                if (existingDocuments < 10) {
                    createSampleDocuments();
                } else {
                    loadDocuments();
                }
            } else {
                Toast.makeText(this, "Erreur lors de la vérification des documents: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createSampleDocuments() {
        String[] titles = {
                "Mathématiques avancées", "Programmation Java", "Base de données",
                "Réseaux informatiques", "Algorithmique", "Développement Mobile",
                "Systèmes d'exploitation", "Intelligence Artificielle",
                "Gestion de projet", "Sécurité informatique"
        };
        String[] descriptions = {
                "Cours sur les équations différentielles", "Introduction à la POO en Java",
                "Conception de bases de données relationnelles", "Protocoles TCP/IP",
                "Algorithmes de tri et recherche", "Développement d'applications Android",
                "Gestion des processus et threads", "Introduction aux réseaux neuronaux",
                "Méthodologies Agile et Scrum", "Principes de cryptographie"
        };
        String[] types = {"Cours", "Cours", "Cours", "Cours", "Cours", "TP", "Cours", "TD", "Cours", "Cours"};

        for (int i = 0; i < 10; i++) {
            Map<String, Object> document = new HashMap<>();
            document.put("titre", titles[i]);
            document.put("description", descriptions[i]);
            document.put("date", "2025-05-29");
            document.put("type", types[i]);

            db.collection("documents").document("cours_" + (i + 1)).set(document)
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erreur lors de la création du document: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
        Toast.makeText(this, "Documents créés avec succès", Toast.LENGTH_SHORT).show();
        loadDocuments();
    }

    private void loadDocuments() {
        documentList.clear();
        fullDocumentList.clear();
        db.collection("documents").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Document doc = document.toObject(Document.class);
                    documentList.add(doc);
                    fullDocumentList.add(doc);
                }
                documentAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Erreur lors du chargement des documents: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterDocuments(String filter) {
        documentList.clear();
        if ("Tous".equals(filter)) {
            documentList.addAll(fullDocumentList);
        } else {
            for (Document doc : fullDocumentList) {
                if (filter.equals(doc.getType())) {
                    documentList.add(doc);
                }
            }
        }
        documentAdapter.notifyDataSetChanged();
    }
}