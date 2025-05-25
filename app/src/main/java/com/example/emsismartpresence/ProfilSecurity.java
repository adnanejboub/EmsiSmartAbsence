package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilSecurity extends AppCompatActivity {

    private EditText inputCurrentPassword, inputNewPassword, inputConfirmPassword;
    private Button btnInfoPersonnelles, btnSecurite, btnUpdatePassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profil_security);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        inputCurrentPassword = findViewById(R.id.input_current_password);
        inputNewPassword = findViewById(R.id.input_new_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        btnInfoPersonnelles = findViewById(R.id.btn_info_personnelles);
        btnSecurite = findViewById(R.id.btn_securite);
        btnUpdatePassword = findViewById(R.id.btn_update_password);

        // Handle tab switching
        btnInfoPersonnelles.setOnClickListener(v -> {
            // Navigate to Profil activity
            startActivity(new Intent(ProfilSecurity.this, Profil.class));
            finish();
        });

        btnSecurite.setOnClickListener(v -> {
            // Already on this tab, do nothing
        });

        // Handle password update
        btnUpdatePassword.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentPassword = inputCurrentPassword.getText().toString().trim();
        String newPassword = inputNewPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Les nouveaux mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Le nouveau mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate the user
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential).addOnSuccessListener(aVoid -> {
            // Re-authentication successful, update the password
            user.updatePassword(newPassword).addOnSuccessListener(aVoid1 -> {
                Toast.makeText(this, "Mot de passe mis à jour avec succès", Toast.LENGTH_SHORT).show();
                // Optionally clear fields or navigate back
                inputCurrentPassword.setText("");
                inputNewPassword.setText("");
                inputConfirmPassword.setText("");
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Échec de la mise à jour du mot de passe : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Mot de passe actuel incorrect", Toast.LENGTH_SHORT).show();
        });
    }
}