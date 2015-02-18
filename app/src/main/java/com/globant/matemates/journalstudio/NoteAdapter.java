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
 * <p/>
 * Created by ariel.cattaneo on 16/02/2015.
 */
public class NoteAdapter extends ArrayAdapter<JournalNote> {

    private final static String LOG_TAG = NoteAdapter.class.getSimpleName();

    Context mContext;
    DatabaseHelper mDBHelper;
    List<JournalNote> notesList;

    public NoteAdapter(Context context, DatabaseHelper dbHelper, List<JournalNote> notesList) {
        super(context, R.layout.note_entry, R.id.text_view_item_note_title, notesList);

        mContext = context;
        mDBHelper = dbHelper;
        this.notesList = notesList;
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

    private void deleteNoteFromDB(JournalNote note) {
        try {
            Dao<JournalNote, Integer> noteDao = mDBHelper.getNoteDao();
            noteDao.delete(note);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateNoteInDB(JournalNote note) {
        try {
            Dao<JournalNote, Integer> noteDao = mDBHelper.getNoteDao();
            noteDao.update(note);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(JournalNote note) {
        super.add(note);

        addNoteToDB(note);
    }

    @Override
    public void insert(JournalNote note, int index) {
        super.insert(note, index);

        addNoteToDB(note);
    }

    @Override
    public void remove(JournalNote note) {
        super.remove(note);

        deleteNoteFromDB(note);
    }


    public void update(JournalNote oldNote, JournalNote newNote) {
        int pos = getPosition(oldNote);
        super.remove(oldNote);
        super.insert(newNote, pos);

        updateNoteInDB(newNote);
    }

}
