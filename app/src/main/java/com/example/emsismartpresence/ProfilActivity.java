package com.example.emsismartpresence;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emsismartpresence.R;

public class ProfilActivity extends AppCompatActivity {

    private Button btnPersonalInfo, btnSecurity;
    private View personalInfoLayout, securityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profil);

        // Initialize views
        btnPersonalInfo = findViewById(R.id.btn_personal_info);
        btnSecurity = findViewById(R.id.btn_security);
        personalInfoLayout = findViewById(R.id.personal_info_layout);
        securityLayout = findViewById(R.id.security_layout);

        // Set initial visibility
        personalInfoLayout.setVisibility(View.VISIBLE);
        securityLayout.setVisibility(View.GONE);

        // Set click listeners
        btnPersonalInfo.setOnClickListener(v -> {
            personalInfoLayout.setVisibility(View.VISIBLE);
            securityLayout.setVisibility(View.GONE);
        });

        btnSecurity.setOnClickListener(v -> {
            personalInfoLayout.setVisibility(View.GONE);
            securityLayout.setVisibility(View.VISIBLE);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}