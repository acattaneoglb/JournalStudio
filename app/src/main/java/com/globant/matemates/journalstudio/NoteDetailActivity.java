package com.globant.matemates.journalstudio;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;


public class NoteDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        if (savedInstanceState == null) {
            NoteDetailFragment fragment;
            if (getIntent().hasExtra(NoteDetailFragment.SELECTED_NOTE)) {
                JournalNote note = getIntent().getParcelableExtra(NoteDetailFragment.SELECTED_NOTE);
                fragment = NoteDetailFragment.newInstance(note, getIntent().getIntExtra(NoteDetailFragment.CONTACT_POSITION, -1));
            } else {
                fragment = new NoteDetailFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

}
