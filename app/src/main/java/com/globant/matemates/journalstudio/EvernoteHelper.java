package com.globant.matemates.journalstudio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.evernote.client.android.AsyncLinkedNoteStoreClient;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.thrift.transport.TTransportException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paula on 18/02/2015.
 */
public class EvernoteHelper {

    public static final String CONSUMER_KEY = "app840";
    public static final String CONSUMER_SECRET = "772fb2669132ed50";
    public static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final String LOGTAG = EvernoteHelper.class.getSimpleName();
    protected final int DIALOG_PROGRESS = 101;

    private Context mContext;
    private ActionBarActivity mActivity;
    private String mSelectedNotebookGuid;

    private OnClientCallback<Note> mNoteCreateCallback = new OnClientCallback<Note>() {
        @Override
        public void onSuccess(Note note) {
            Toast.makeText(mContext, mContext.getString(R.string.notification_note_saved), Toast.LENGTH_LONG).show();
            mActivity.removeDialog(DIALOG_PROGRESS);
        }

        @Override
        public void onException(Exception exception) {
            Log.e(LOGTAG, mContext.getString(R.string.notification_note_failed, exception));
            Toast.makeText(mContext, mContext.getString(R.string.notification_note_failed), Toast.LENGTH_LONG).show();
            mActivity.removeDialog(DIALOG_PROGRESS);
        }
    };

    public EvernoteHelper(Context context, ActionBarActivity activity){
        mContext = context;
        mActivity = activity;
    }

    public void selectNotebook(final EvernoteSession session, final String title, final String content, final byte[] imageByte) {
        if(session.isAppLinkedNotebook()) {
            Toast.makeText(mContext, mContext.getString(R.string.error_notebooks_list), Toast.LENGTH_LONG).show();
            return;
        }

        try {
            session.getClientFactory().createNoteStoreClient().listNotebooks(new OnClientCallback<List<Notebook>>() {
                int mSelectedPos = -1;

                @Override
                public void onSuccess(final List<Notebook> notebooks) {
                    CharSequence[] names = new CharSequence[notebooks.size()];
                    int selected = -1;
                    Notebook notebook = null;
                    for (int index = 0; index < notebooks.size(); index++) {
                        notebook = notebooks.get(index);
                        names[index] = notebook.getName();
                        if (notebook.getGuid().equals(mSelectedNotebookGuid)) {
                            selected = index;
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder
                            .setSingleChoiceItems(names, selected, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mSelectedPos = which;
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mSelectedPos > -1) {
                                        mSelectedNotebookGuid = notebooks.get(mSelectedPos).getGuid();
                                        saveNote(session, title, content, imageByte);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }

                @Override
                public void onException(Exception exception) {
                    Log.e(LOGTAG, mContext.getString(R.string.error_notebooks_list), exception);
                    Toast.makeText(mContext, mContext.getString(R.string.error_notebooks_list), Toast.LENGTH_LONG).show();
                    mActivity.removeDialog(DIALOG_PROGRESS);
                }
            });
        } catch (TTransportException exception) {
            Log.e(LOGTAG, mContext.getString(R.string.error_notestore_create), exception);
            Toast.makeText(mContext, mContext.getString(R.string.error_notestore_create), Toast.LENGTH_LONG).show();
            mActivity.removeDialog(DIALOG_PROGRESS);
        }
    }

    public void saveNote(EvernoteSession session, String title, String content, byte[] imageByte) {
        String tag;

        Note note = new Note();
        note.setTitle(title);

        //TODO: Creating data
        Data data = new Data();
        data.setBodyHash(EvernoteUtil.hash(imageByte));
        data.setBody(imageByte);

        //TODO: Creating resource
        Resource resource = new Resource();
        resource.setMime("image/png");
        resource.setData(data);

        tag = EvernoteUtil.createEnMediaTag(resource);

        List<Resource> resourceList = new ArrayList<>();
        resourceList.add(resource);
        note.setResources(resourceList);

        //TODO: line breaks need to be converted to render in ENML
        String noteBody = EvernoteUtil.NOTE_PREFIX + content + "<br/>" + tag + EvernoteUtil.NOTE_SUFFIX;

        note.setContent(noteBody);

        if(!session.getAuthenticationResult().isAppLinkedNotebook()) {
            //If User has selected a notebook guid, assign it now
            if (!TextUtils.isEmpty(mSelectedNotebookGuid)) {
                note.setNotebookGuid(mSelectedNotebookGuid);
            }
            mActivity.showDialog(DIALOG_PROGRESS);
            try {
                session.getClientFactory().createNoteStoreClient().createNote(note, mNoteCreateCallback);
            } catch (TTransportException exception) {
                Log.e(LOGTAG, mContext.getString(R.string.error_notestore_create), exception);
                Toast.makeText(mContext, mContext.getString(R.string.error_notestore_create), Toast.LENGTH_LONG).show();
                mActivity.removeDialog(DIALOG_PROGRESS);
            }
        } else {
            createNoteInAppLinkedNotebook(session, note, mNoteCreateCallback);
        }
    }

    private void createNoteInAppLinkedNotebook(EvernoteSession session, final Note note, final OnClientCallback<Note> createNoteCallback) {
        mActivity.showDialog(DIALOG_PROGRESS);
        invokeOnAppLinkedNotebook(session, new OnClientCallback<Pair<AsyncLinkedNoteStoreClient, LinkedNotebook>>() {
            @Override
            public void onSuccess(final Pair<AsyncLinkedNoteStoreClient, LinkedNotebook> pair) {
                // Rely on the callback to dismiss the dialog
                pair.first.createNoteAsync(note, pair.second, createNoteCallback);
            }

            @Override
            public void onException(Exception exception) {
                Log.e(LOGTAG, mContext.getString(R.string.error_note_create_linked_notebook), exception);
                Toast.makeText(mContext, mContext.getString(R.string.error_note_create_linked_notebook), Toast.LENGTH_LONG).show();
                mActivity.removeDialog(DIALOG_PROGRESS);
            }
        });
    }

    private void invokeOnAppLinkedNotebook(final EvernoteSession session, final OnClientCallback<Pair<AsyncLinkedNoteStoreClient, LinkedNotebook>> callback) {
        try {
            // We need to get the one and only linked notebook
            session.getClientFactory().createNoteStoreClient().listLinkedNotebooks(new OnClientCallback<List<LinkedNotebook>>() {
                @Override
                public void onSuccess(List<LinkedNotebook> linkedNotebooks) {
                    // We should only have one linked notebook
                    if (linkedNotebooks.size() != 1) {
                        Log.e(LOGTAG, "Error getting linked notebook - more than one linked notebook");
                        callback.onException(new Exception("Not single linked notebook"));
                    } else {
                        final LinkedNotebook linkedNotebook = linkedNotebooks.get(0);
                        session.getClientFactory().createLinkedNoteStoreClientAsync(linkedNotebook, new OnClientCallback<AsyncLinkedNoteStoreClient>() {
                            @Override
                            public void onSuccess(AsyncLinkedNoteStoreClient asyncLinkedNoteStoreClient) {
                                // Finally create the note in the linked notebook
                                callback.onSuccess(new Pair<AsyncLinkedNoteStoreClient, LinkedNotebook>(asyncLinkedNoteStoreClient, linkedNotebook));
                            }

                            @Override
                            public void onException(Exception exception) {
                                callback.onException(exception);
                            }
                        });
                    }
                }

                @Override
                public void onException(Exception exception) {
                    callback.onException(exception);
                }
            });
        } catch (TTransportException exception) {
            callback.onException(exception);
        }
    }

}
