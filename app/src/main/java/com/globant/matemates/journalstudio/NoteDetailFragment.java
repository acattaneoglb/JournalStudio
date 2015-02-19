package com.globant.matemates.journalstudio;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
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

import com.evernote.client.android.EvernoteSession;

import java.io.ByteArrayOutputStream;

/**
 * A placeholder fragment containing a simple view.
 */
public class NoteDetailFragment extends Fragment {

    public static final String TAG = "NOTE_DETAIL_FRAGMENT";
    public static final String SELECTED_NOTE = "SELECTED_NOTE";
    public static final String NEW_NOTE = "NEW_NOTE";
    public static final String NOTE_POSITION = "NOTE_POSITION";
    public static final String MODIFY_NOTE = "MODIFY_NOTE";

    public static final int RESULT_CODE_NEW_NOTE = 10;
    public static final int RESULT_CODE_MODIFY_NOTE = 11;

    private static final int REQUEST_CODE_CAMERA = 1337;

    EditText mNoteTitle, mNoteText;
    ImageView mNoteImage;
    Button mButtonModifyAdd, mButtonDelete;
    ImageButton mButtonCamera;
    Bitmap mImageShared;

    private JournalNote mSelectedNote;
    private EvernoteSession mEvernoteSession;
    private EvernoteHelper evernoteHelper;

    public NoteDetailFragment() {
    }

    public static NoteDetailFragment newInstance(JournalNote note, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(NOTE_POSITION, position);
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
        prepareEvernoteHandler();
        return rootView;
    }

    private void prepareEvernoteHandler() {
        evernoteHelper = new EvernoteHelper(getActivity(), (ActionBarActivity) getActivity());
        mEvernoteSession = EvernoteSession.getInstance(getActivity(), EvernoteHelper.CONSUMER_KEY,
                EvernoteHelper.CONSUMER_SECRET, EvernoteHelper.EVERNOTE_SERVICE, true);
    }

    private void editOrAddNew(boolean isNotNew) {
        if (isNotNew) {
            mSelectedNote = getArguments().getParcelable(SELECTED_NOTE);
            mNoteTitle.setText(mSelectedNote.getTitle());
            mNoteText.setText(mSelectedNote.getText());
            mNoteImage.setImageBitmap(mSelectedNote.getImage());
            mImageShared = mSelectedNote.getImage();
            mButtonDelete.setVisibility(View.VISIBLE);
            mButtonModifyAdd.setText(getString(R.string.button_modify));
        } else {
            mSelectedNote = null;
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
                    getActivity().setResult(RESULT_CODE_NEW_NOTE, result);
                    getActivity().finish();
                } else {
                    if (mSelectedNote != null) {
                        mSelectedNote.setTitle(mNoteTitle.getText().toString());
                        mSelectedNote.setText(mNoteText.getText().toString());
                        Bitmap noteImageBitmap = ((BitmapDrawable) mNoteImage.getDrawable()).getBitmap();
                        mSelectedNote.setImage(noteImageBitmap);
                        mImageShared = noteImageBitmap;
                        Intent result = new Intent();
                        result.putExtra(MODIFY_NOTE, mSelectedNote);
                        Log.d("coso",mSelectedNote.getText());
                        getActivity().setResult(RESULT_CODE_MODIFY_NOTE, result);
                        result.putExtra(NOTE_POSITION, getArguments().getInt(NOTE_POSITION));
                        getActivity().finish();
                    }
                }
            }
        });
    }

    private void deleteButton() {
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getArguments().getInt(NOTE_POSITION);
                if (position != -1) {
                    Intent result = new Intent();
                    result.putExtra(NOTE_POSITION, getArguments().getInt(NOTE_POSITION));
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
                    mImageShared = imageBitmap;
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
                if (mEvernoteSession.isLoggedIn()) {
                    shareNoteOnEvernote();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.action_share_note_warning), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_login:
                mEvernoteSession.authenticate(getActivity());
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareNoteOnEvernote() {
        evernoteHelper.selectNotebook(mEvernoteSession, mNoteTitle.getText().toString(), mNoteText.getText().toString(),
                convertBitmapImageToByteArray(mImageShared));
//        evernoteHelper.saveNote(mEvernoteSession, mNoteTitle.getText().toString(), mNoteText.getText().toString(),
//                convertBitmapImageToByteArray(mImageShared));
    }

    private byte[] convertBitmapImageToByteArray(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
