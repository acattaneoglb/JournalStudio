package com.globant.matemates.journalstudio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by carlos.pienovi on 19/02/2015.
 */
public class SettingsActivity extends CustomActivity {

    public static class AppSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }

        private boolean isTheme(String theme) {
            String[] themes = getResources().getStringArray(R.array.theme_options);
            for (String s : themes) {
                if (s.equals(theme)) return true;
            }
            return false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            String theme = sharedPreferences.getString(key, getString(R.string.def));
            if (isTheme(theme)) {
                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), getActivity().getClass()));
                getActivity().overridePendingTransition(0, 0);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction()
                .replace(R.id.settings, new AppSettingsFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), JournalActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        SettingsActivity.this.finish();
    }

}
