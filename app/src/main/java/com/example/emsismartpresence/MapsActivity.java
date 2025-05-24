package com.example.emsismartpresence;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MapsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Ajouter le fragment Maps dynamiquement si ce n'est pas déjà fait
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new Maps()) // R.id.fragment_container sera dans le layout
                    .commit();
        }
    }
}
