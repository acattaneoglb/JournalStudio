package com.globant.matemates.journalstudio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class NoteDetailFragment extends Fragment {

    public static final String SELECTED_NOTE = "SELECTED_NOTE";

    TextView mNoteTitle, mNoteText;

    public NoteDetailFragment() {
    }

    public static NoteDetailFragment newInstance(JournalNote note) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTED_NOTE, note);
        NoteDetailFragment fragment = new NoteDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_detail, container, false);
        init(rootView);
        JournalNote note = getArguments().getParcelable(SELECTED_NOTE);
        mNoteTitle.setText(note.getTitle());
        mNoteText.setText(note.getText());
        return rootView;
    }

    private void init(View rootView) {
        mNoteTitle = (TextView) rootView.findViewById(R.id.note_title);
        mNoteText = (TextView) rootView.findViewById(R.id.note_text);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_note_detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_note:
                Toast.makeText(getActivity(), "DELETING NOTE", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
