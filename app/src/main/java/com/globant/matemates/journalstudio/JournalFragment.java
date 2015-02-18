package com.globant.matemates.journalstudio;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class JournalFragment extends ListFragment {

    private static final int REQUEST_CODE_NEW_NOTE = 1;
    private static final int REQUEST_CODE_EDIT_NOTE = 2;

    NoteAdapter mAdapter;
    DatabaseHelper mDBHelper = null;

    public JournalFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<JournalNote> notes;
        try {
            notes = getDBHelper().getNoteDao().queryForAll();
        } catch (SQLException e) {
            notes = new ArrayList<>();
            e.printStackTrace();
        }
        mAdapter = new NoteAdapter(getActivity(), getDBHelper(), notes);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), NoteDetailActivity.class);
        JournalNote selectedNote = mAdapter.getItem(position);
        i.putExtra(NoteDetailFragment.SELECTED_NOTE, selectedNote);
        i.putExtra(NoteDetailFragment.CONTACT_POSITION, position);
        startActivityForResult(i, REQUEST_CODE_EDIT_NOTE);
    }

    public DatabaseHelper getDBHelper() {
        if (mDBHelper == null) {
            mDBHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return mDBHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_journal_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_note:
                Intent i = new Intent(getActivity(), NoteDetailActivity.class);
                startActivityForResult(i, REQUEST_CODE_NEW_NOTE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_NEW_NOTE:
                if (resultCode == Activity.RESULT_OK) {
                    JournalNote newNote = data.getParcelableExtra(NoteDetailFragment.NEW_NOTE);
                    mAdapter.add(newNote);
                }
                break;
            case REQUEST_CODE_EDIT_NOTE:
                if (resultCode == Activity.RESULT_OK) {
                    int position = data.getIntExtra(NoteDetailFragment.CONTACT_POSITION, -1);
                    Log.d("coso", "position en Result = " + position);
                    if (position != -1) {
                        mAdapter.remove(mAdapter.getItem(position));
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (mDBHelper != null) {
            OpenHelperManager.releaseHelper();
            mDBHelper = null;
        }
        super.onDestroy();
    }
}
