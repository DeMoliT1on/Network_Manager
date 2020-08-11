package com.dhruv.networkmanager.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.dhruv.networkmanager.R;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);
    }
}
