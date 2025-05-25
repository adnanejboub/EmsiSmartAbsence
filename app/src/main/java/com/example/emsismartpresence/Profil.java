package com.example.emsismartpresence;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Profil extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPhone, inputAddress, inputCity;
    private ImageView profilePicture, btnUpload;
    private Button btnInfoPersonnelles, btnSecurite, btnSave, btnCancel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPhone = findViewById(R.id.input_phone);
        inputAddress = findViewById(R.id.input_address);
        inputCity = findViewById(R.id.input_city);
        profilePicture = findViewById(R.id.profile_picture);
        btnUpload = findViewById(R.id.btn_upload);
        btnInfoPersonnelles = findViewById(R.id.btn_info_personnelles);
        btnSecurite = findViewById(R.id.btn_securite);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // Set up image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    profilePicture.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Toast.makeText(this, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load user data
        loadUserData();

        // Handle tab switching
        btnInfoPersonnelles.setOnClickListener(v -> {
            // Already on this tab, do nothing
        });

        btnSecurite.setOnClickListener(v -> {
            // Navigate to SecurityProfil activity
            startActivity(new Intent(Profil.this, ProfilSecurity.class));
        });

        // Handle image upload
        btnUpload.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        // Handle save button
        btnSave.setOnClickListener(v -> saveUserData());

        // Handle cancel button
        btnCancel.setOnClickListener(v -> loadUserData()); // Reset fields to original values
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Populate fields with user data
                    inputName.setText(documentSnapshot.getString("fullName"));
                    inputEmail.setText(documentSnapshot.getString("email"));
                    inputPhone.setText(documentSnapshot.getString("phone"));
                    inputAddress.setText(documentSnapshot.getString("address"));
                    inputCity.setText(documentSnapshot.getString("city"));

                    // Load profile picture if exists
                    String base64Image = documentSnapshot.getString("profilePicture");
                    if (base64Image != null && !base64Image.isEmpty()) {
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profilePicture.setImageBitmap(bitmap);
                    }
                } else {
                    Toast.makeText(this, "Aucune donnée trouvée pour cet utilisateur", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Erreur lors du chargement des données", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            // Prepare updated data
            Map<String, Object> userData = new HashMap<>();
            userData.put("fullName", inputName.getText().toString().trim());
            userData.put("email", inputEmail.getText().toString().trim());
            userData.put("phone", inputPhone.getText().toString().trim());
            userData.put("address", inputAddress.getText().toString().trim());
            userData.put("city", inputCity.getText().toString().trim());

            // Convert profile picture to Base64
            profilePicture.setDrawingCacheEnabled(true);
            profilePicture.buildDrawingCache();
            Bitmap bitmap = profilePicture.getDrawingCache();
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                userData.put("profilePicture", base64Image);
            }

            // Save to Firestore
            userRef.set(userData).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Erreur lors de la mise à jour du profil", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
        }
    }
}