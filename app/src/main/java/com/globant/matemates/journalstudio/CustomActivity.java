package com.globant.matemates.journalstudio;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by carlos.pienovi on 19/02/2015.
 */
public class CustomActivity extends ActionBarActivity {

    private static final String THEME_PREFERENCE = "theme_preference";

    String mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTheme = sharedPreferences.getString(THEME_PREFERENCE, getString(R.string.def));
        setThemeByString();
        super.onCreate(savedInstanceState);
    }

    private void setThemeByString() {
        if (mTheme.equals(getString(R.string.def))) {
            setTheme(R.style.AppTheme);
        }
        if (mTheme.equals(getString(R.string.winter))) {
            setTheme(R.style.AppThemeWinter);
        }
        if (mTheme.equals(getString(R.string.summer))) {
            setTheme(R.style.AppThemeSummer);
        }
        if (mTheme.equals(getString(R.string.fall))) {
            setTheme(R.style.AppThemeFall);
        }
        if (mTheme.equals(getString(R.string.spring))) {
            setTheme(R.style.AppThemeSpring);
        }
    }

    public Drawable getButtonTheme() {
        if (mTheme.equals(getString(R.string.winter))) {
            return getResources().getDrawable(R.drawable.button_winter_theme);
        }
        if (mTheme.equals(getString(R.string.summer))) {
            return getResources().getDrawable(R.drawable.button_summer_theme);
        }
        if (mTheme.equals(getString(R.string.fall))) {
            return getResources().getDrawable(R.drawable.button_fall_theme);
        }
        if (mTheme.equals(getString(R.string.spring))) {
            return getResources().getDrawable(R.drawable.button_spring_theme);
        }
        //if (mTheme.equals(getString(R.string.def))) {
            return null;
        //}
    }
}
