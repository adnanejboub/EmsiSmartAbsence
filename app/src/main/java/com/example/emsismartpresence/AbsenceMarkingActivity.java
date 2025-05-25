package com.example.emsismartpresence;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbsenceMarkingActivity extends AppCompatActivity {
    private static final String TAG = "AbsenceMarkingActivity";
    private Spinner groupSpinner, siteSpinner;
    private TextView dateText;
    private AppCompatButton selectDateButton, saveButton;
    private RecyclerView studentsRecyclerView;
    private EditText remarksEditText;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LocalDate selectedDate;

    private static final List<String> VALID_SITES = Arrays.asList(
            "Centre_1", "Centre_2", "Moulay_Youssef", "Berrinzrane", "Roudani", "Les_Orangers"
    );
    private static final List<String> GROUPS = Arrays.asList(
            "4IIR-A", "4IIR-B", "3IIR-A", "1AP-A"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_marking);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurer la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(AbsenceMarkingActivity.this, Home.class);
            startActivity(intent);
            finish();
        });

        // Initialiser les vues
        groupSpinner = findViewById(R.id.spinner_group);
        siteSpinner = findViewById(R.id.spinner_site);
        dateText = findViewById(R.id.date_text);
        selectDateButton = findViewById(R.id.btn_select_date);
        studentsRecyclerView = findViewById(R.id.students_recycler_view);
        remarksEditText = findViewById(R.id.remarks_edit_text);
        saveButton = findViewById(R.id.btn_save);

        // Configurer les Spinners
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, GROUPS);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(groupAdapter);

        ArrayAdapter<String> siteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, VALID_SITES);
        siteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        siteSpinner.setAdapter(siteAdapter);

        // Configurer le RecyclerView
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsRecyclerView.setAdapter(studentAdapter);

        // Charger les étudiants quand un groupe est sélectionné
        groupSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadStudents(GROUPS.get(position));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Configurer le sélecteur de date
        selectDateButton.setOnClickListener(v -> showDatePicker());

        // Configurer le bouton Enregistrer
        saveButton.setOnClickListener(v -> saveAbsences());

        // Charger les étudiants par défaut pour le premier groupe
        loadStudents(GROUPS.get(0));
    }

    private void loadStudents(String groupId) {
        studentList.clear();
        switch (groupId) {
            case "4IIR-A":
                // Site principal : Centre_1
                studentList.addAll(Arrays.asList(
                        new Student("S001", "Ahmed Benali", false),
                        new Student("S002", "Sara El Idrissi", false),
                        new Student("S003", "Youssef Amrani", false),
                        new Student("S004", "Fatima Zahra Oulad", false),
                        new Student("S005", "Mohamed Amine", false),
                        new Student("S006", "Khadija Laaroussi", false),
                        new Student("S007", "Omar Tazi", false),
                        new Student("S008", "Aya Bousfiha", false),
                        new Student("S009", "Ilyas Cherkaoui", false),
                        new Student("S010", "Hiba Naciri", false),
                        new Student("S011", "Rachid Bensaid", false),
                        new Student("S012", "Nour El Houda", false),
                        new Student("S013", "Zineb El Amrani", false),
                        new Student("S014", "Adil Saidi", false),
                        new Student("S015", "Laila Benkirane", false),
                        new Student("S016", "Hamza El Fassi", false),
                        new Student("S017", "Asmaa Ouazzani", false),
                        new Student("S018", "Bilal Essaid", false),
                        new Student("S019", "Meryem Hajji", false),
                        new Student("S020", "Karim Lahlou", false),
                        new Student("S021", "Sanaa El Yazidi", false),
                        new Student("S022", "Tarek Bennani", false),
                        new Student("S023", "Imane Chraibi", false)
                ));
                break;
            case "4IIR-B":
                // Site principal : Roudani
                studentList.addAll(Arrays.asList(
                        new Student("S024", "Hassan El Kadi", false),
                        new Student("S025", "Nadia Ait Ali", false),
                        new Student("S026", "Yassine Bouziane", false),
                        new Student("S027", "Salma El Ouardi", false),
                        new Student("S028", "Abdelilah Rami", false),
                        new Student("S029", "Chaimae El Hajji", false),
                        new Student("S030", "Reda Benhaddou", false),
                        new Student("S031", "Amal El Alaoui", false),
                        new Student("S032", "Oussama El Hilali", false),
                        new Student("S033", "Kenza Lmoudden", false),
                        new Student("S034", "Ismail El Ghazi", false),
                        new Student("S035", "Hajar El Bernoussi", false),
                        new Student("S036", "Zakaria El Malki", false),
                        new Student("S037", "Rania Benjelloun", false),
                        new Student("S038", "Sofiane El Khattabi", false),
                        new Student("S039", "Lina El Moutawakil", false),
                        new Student("S040", "Anas El Bouanani", false),
                        new Student("S041", "Mouna El Kadiri", false),
                        new Student("S042", "Younes El Hassani", false),
                        new Student("S043", "Safae El Omari", false),
                        new Student("S044", "Marouane El Aouni", false)
                ));
                break;
            case "3IIR-A":
                // Site principal : Moulay_Youssef
                studentList.addAll(Arrays.asList(
                        new Student("S045", "Othmane El Mesbahi", false),
                        new Student("S046", "Amina El Filali", false),
                        new Student("S047", "Khalid El Hammoumi", false),
                        new Student("S048", "Wiam El Ghaouti", false),
                        new Student("S049", "Badr Eddine El Hachimi", false),
                        new Student("S050", "Soukaina El Mansouri", false),
                        new Student("S051", "Abderrahmane El Ouazzani", false),
                        new Student("S052", "Rim El Qadi", false),
                        new Student("S053", "Ibrahim El Rhazi", false),
                        new Student("S054", "Nisrine El Saidi", false),
                        new Student("S055", "Mehdi El Yousfi", false),
                        new Student("S056", "Hafsa El Zaimi", false),
                        new Student("S057", "Yassir El Amraoui", false),
                        new Student("S058", "Asmae El Boukili", false),
                        new Student("S059", "Taha El Fath", false),
                        new Student("S060", "Chaimae El Gharbaoui", false),
                        new Student("S061", "Ayoub El Hmidi", false),
                        new Student("S062", "Fadwa El Idrissi", false),
                        new Student("S063", "Nabil El Jaouhari", false),
                        new Student("S064", "Zahra El Kabbaj", false),
                        new Student("S065", "Omar El Khattabi", false),
                        new Student("S066", "Meryem El Mernissi", false)
                ));
                break;
            case "1AP-A":
                // Site principal : Les_Orangers
                studentList.addAll(Arrays.asList(
                        new Student("S067", "Adam El Amine", false),
                        new Student("S068", "Ines El Asri", false),
                        new Student("S069", "Yahya El Badaoui", false),
                        new Student("S070", "Loubna El Baraka", false),
                        new Student("S071", "Sofian El Bazi", false),
                        new Student("S072", "Amira El Belkacemi", false),
                        new Student("S073", "Ilias El Bouazzaoui", false),
                        new Student("S074", "Houda El Bouhali", false),
                        new Student("S075", "Anwar El Bourkadi", false),
                        new Student("S076", "Siham El Boutaybi", false),
                        new Student("S077", "Youness El Charkaoui", false),
                        new Student("S078", "Kawtar El Charrat", false),
                        new Student("S079", "Hamid El Dahbi", false),
                        new Student("S080", "Fatima El Dahmani", false),
                        new Student("S081", "Saad El Fassi", false),
                        new Student("S082", "Naima El Fath", false),
                        new Student("S083", "Rayan El Ghazali", false),
                        new Student("S084", "Zineb El Ghoulam", false),
                        new Student("S085", "Yassine El Habti", false),
                        new Student("S086", "Aicha El Haddad", false),
                        new Student("S087", "Bilal El Hajji", false),
                        new Student("S088", "Rihab El Hamdi", false),
                        new Student("S089", "Imad El Hammouti", false),
                        new Student("S090", "Salima El Hassnaoui", false)
                ));
                break;
        }
        studentAdapter.notifyDataSetChanged();
    }

    private void showDatePicker() {
        LocalDate today = LocalDate.now();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    dateText.setText(selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                },
                today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth()
        );
        datePickerDialog.show();
    }

    private void saveAbsences() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String groupId = groupSpinner.getSelectedItem().toString();
        String siteId = siteSpinner.getSelectedItem().toString();
        String remarks = remarksEditText.getText().toString().trim();
        String date = selectedDate != null ? selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;

        if (date == null) {
            Toast.makeText(this, "Veuillez sélectionner une date", Toast.LENGTH_SHORT).show();
            return;
        }

        AbsenceEntry absenceEntry = new AbsenceEntry(groupId, siteId, date, studentList, remarks);

        db.collection("absences")
                .document(currentUser.getUid())
                .collection("absence_entries")
                .document()
                .set(absenceEntry)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Absences enregistrées avec succès");
                    Toast.makeText(this, "Absences enregistrées", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors de l'enregistrement: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}