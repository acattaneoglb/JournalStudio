package com.globant.matemates.journalstudio;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;


public class NoteDetailActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayShowHomeEnabled(true);
        if (savedInstanceState == null) {
            NoteDetailFragment fragment;
            if (getIntent().hasExtra(NoteDetailFragment.SELECTED_NOTE)) {
                JournalNote note = getIntent().getParcelableExtra(NoteDetailFragment.SELECTED_NOTE);
                fragment = NoteDetailFragment.newInstance(note, getIntent().getIntExtra(NoteDetailFragment.NOTE_POSITION, -1));
            } else {
                fragment = new NoteDetailFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, NoteDetailFragment.TAG)
                    .commit();
        }
    }

}
