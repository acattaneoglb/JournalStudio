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
            JournalNote note = getIntent().getParcelableExtra(NoteDetailFragment.SELECTED_NOTE);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, NoteDetailFragment.newInstance(note))
                    .commit();
        }
    }

}
