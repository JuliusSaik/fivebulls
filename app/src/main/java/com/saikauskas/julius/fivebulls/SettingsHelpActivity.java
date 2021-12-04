package com.saikauskas.julius.fivebulls;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SettingsHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_help);

        FloatingActionButton fabBack = findViewById(R.id.fabDisclaimerBack);

        fabBack.setOnClickListener(view -> {
            onBackPressed();
        });

    }
}