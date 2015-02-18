package com.globant.matemates.journalstudio;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class NoteDetailFragment extends Fragment {

    public static final String SELECTED_NOTE = "SELECTED_NOTE";
    public static final String NEW_NOTE = "NEW_NOTE";
    public static final String CONTACT_POSITION = "CONTACT_POSITION";

    private static final int REQUEST_CODE_CAMERA = 1337;

    EditText mNoteTitle, mNoteText;
    ImageView mNoteImage;
    Button mButtonModifyAdd, mButtonDelete;
    ImageButton mButtonCamera;

    public NoteDetailFragment() {
    }

    public static NoteDetailFragment newInstance(JournalNote note, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(CONTACT_POSITION, position);
        bundle.putParcelable(SELECTED_NOTE, note);
        NoteDetailFragment fragment = new NoteDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_detail, container, false);
        init(rootView);
        editOrAddNew(getArguments() != null);
        addModifyButton();
        cameraButton();
        deleteButton();
        return rootView;
    }

    private void editOrAddNew(boolean isNew) {
        if (isNew) {
            JournalNote note = getArguments().getParcelable(SELECTED_NOTE);
            mNoteTitle.setText(note.getTitle());
            mNoteText.setText(note.getText());
            mNoteImage.setImageBitmap(note.getImage());
            mButtonDelete.setVisibility(View.VISIBLE);
            mButtonModifyAdd.setText(getString(R.string.button_modify));
        } else {
            mButtonDelete.setVisibility(View.GONE);
            mButtonModifyAdd.setText(getString(R.string.button_add));
        }
    }

    private void addModifyButton() {
        mButtonModifyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonModifyAdd.getText().equals(getString(R.string.button_add))) {
                    JournalNote note = new JournalNote();
                    note.setTitle(mNoteTitle.getText().toString());
                    note.setText(mNoteText.getText().toString());
                    Bitmap noteImageBitmap = ((BitmapDrawable) mNoteImage.getDrawable()).getBitmap();
                    note.setImage(noteImageBitmap);
                    Intent result = new Intent();
                    result.putExtra(NEW_NOTE, note);
                    getActivity().setResult(Activity.RESULT_OK, result);
                    getActivity().finish();
                } else {

                }
            }
        });
    }

    private void deleteButton() {
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getArguments().getInt(CONTACT_POSITION);
                Log.d("coso", "position en NoteDetailFragment = " + position);
                if (position != -1) {
                    Intent result = new Intent();
                    result.putExtra(CONTACT_POSITION, getArguments().getInt(CONTACT_POSITION));
                    getActivity().setResult(Activity.RESULT_OK, result);
                    getActivity().finish();
                }
            }
        });
    }

    private void cameraButton() {
        mButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    mNoteImage.setImageBitmap(imageBitmap);
                }
                break;
        }
    }

    private void init(View rootView) {
        mNoteTitle = (EditText) rootView.findViewById(R.id.note_title);
        mNoteText = (EditText) rootView.findViewById(R.id.note_text);
        mNoteImage = (ImageView) rootView.findViewById(R.id.note_image);
        mButtonCamera = (ImageButton) rootView.findViewById(R.id.button_camera);
        mButtonDelete = (Button) rootView.findViewById(R.id.button_delete);
        mButtonModifyAdd = (Button) rootView.findViewById(R.id.button_modify_add);
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
            case R.id.action_share_note:
                Toast.makeText(getActivity(), "SHARING NOTE", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
