package com.globant.matemates.journalstudio;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Adapter for the note class.
 *
 * Created by ariel.cattaneo on 16/02/2015.
 */
public class NoteAdapter extends ArrayAdapter<JournalNote> {

    private final static String LOG_TAG = NoteAdapter.class.getSimpleName();

    Context mContext;
    DatabaseHelper mDBHelper;

    public NoteAdapter(Context context, DatabaseHelper dbHelper, List<JournalNote> notesList) {
        super(context, R.layout.note_entry, notesList);

        mContext = context;
        mDBHelper = dbHelper;
    }

    private void addNoteToDB(JournalNote note) {
        try {
            Dao<JournalNote, Integer> noteDao = mDBHelper.getNoteDao();
            noteDao.create(note);
        } catch (SQLException e) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.user_message_error_adding_note),
                    Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Exception adding note to DB: " + e.getMessage());
        }
    }

    @Override
    public void add(JournalNote note) {
        super.add(note);

        addNoteToDB(note);
    }

}
