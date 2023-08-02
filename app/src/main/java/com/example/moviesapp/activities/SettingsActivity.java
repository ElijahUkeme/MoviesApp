package com.example.moviesapp.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

import com.example.moviesapp.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
