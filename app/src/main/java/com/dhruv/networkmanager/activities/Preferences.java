package com.dhruv.networkmanager.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.fragments.PreferencesFragment;

public class Preferences extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferenceViewer, new PreferencesFragment())
                .commit();
    }
}
