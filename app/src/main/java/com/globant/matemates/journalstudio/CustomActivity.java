package com.globant.matemates.journalstudio;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by carlos.pienovi on 19/02/2015.
 */
public class CustomActivity extends ActionBarActivity {

    private static final String THEME_PREFERENCE = "theme_preference";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString(THEME_PREFERENCE, getString(R.string.def));
        setThemeByString(theme);
        super.onCreate(savedInstanceState);
    }

    private void setThemeByString(String theme) {
        if (theme.equals(getString(R.string.def))) {
            setTheme(R.style.AppTheme);
        }
        if (theme.equals(getString(R.string.cold))) {
            setTheme(R.style.AppThemeCold);
        }
        if (theme.equals(getString(R.string.hot))) {
            setTheme(R.style.AppThemeHot);
        }
    }
}
