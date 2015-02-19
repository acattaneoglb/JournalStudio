package com.globant.matemates.journalstudio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paula on 18/02/2015.
 */
public class EvernoteHelper {

    private static final String CONSUMER_KEY = "app791";
    private static final String CONSUMER_SECRET = "47dd457bf08c50da";
    private static final String LOGTAG = EvernoteHelper.class.getSimpleName();
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    protected final int DIALOG_PROGRESS = 101;

    private EvernoteSession mEvernoteSession;
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
        mEvernoteSession = EvernoteSession.getInstance(context, CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_SERVICE, true);
    }

    private void selectNotebook() {
        if(mEvernoteSession.isAppLinkedNotebook()) {
            Toast.makeText(mContext, mContext.getString(R.string.error_notebooks_list), Toast.LENGTH_LONG).show();
            return;
        }

        try {
            mEvernoteSession.getClientFactory().createNoteStoreClient().listNotebooks(new OnClientCallback<List<Notebook>>() {
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

    public void saveNote(String title, String content, byte[] imageByte) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(mContext, mContext.getString(R.string.warning_empty_content), Toast.LENGTH_LONG).show();
            return;
        }

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

        String tag = EvernoteUtil.createEnMediaTag(resource);

        List<Resource> resourceList = new ArrayList<>();
        resourceList.add(resource);
        note.setResources(resourceList);

        //TODO: line breaks need to be converted to render in ENML
        String noteBody = EvernoteUtil.NOTE_PREFIX + content + "<br/>" + tag + EvernoteUtil.NOTE_SUFFIX;

        note.setContent(noteBody);

        if(!mEvernoteSession.getAuthenticationResult().isAppLinkedNotebook()) {
            //If User has selected a notebook guid, assign it now
            if (!TextUtils.isEmpty(mSelectedNotebookGuid)) {
                note.setNotebookGuid(mSelectedNotebookGuid);
            }
            mActivity.showDialog(DIALOG_PROGRESS);
            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().createNote(note, mNoteCreateCallback);
            } catch (TTransportException exception) {
                Log.e(LOGTAG, mContext.getString(R.string.error_notestore_create), exception);
                Toast.makeText(mContext, mContext.getString(R.string.error_notestore_create), Toast.LENGTH_LONG).show();
                mActivity.removeDialog(DIALOG_PROGRESS);
            }
        } else {
            createNoteInAppLinkedNotebook(note, mNoteCreateCallback);
        }
    }

    protected void createNoteInAppLinkedNotebook(final Note note, final OnClientCallback<Note> createNoteCallback) {
        mActivity.showDialog(DIALOG_PROGRESS);
        invokeOnAppLinkedNotebook(new OnClientCallback<Pair<AsyncLinkedNoteStoreClient, LinkedNotebook>>() {
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

    protected void invokeOnAppLinkedNotebook(final OnClientCallback<Pair<AsyncLinkedNoteStoreClient, LinkedNotebook>> callback) {
        try {
            // We need to get the one and only linked notebook
            mEvernoteSession.getClientFactory().createNoteStoreClient().listLinkedNotebooks(new OnClientCallback<List<LinkedNotebook>>() {
                @Override
                public void onSuccess(List<LinkedNotebook> linkedNotebooks) {
                    // We should only have one linked notebook
                    if (linkedNotebooks.size() != 1) {
                        Log.e(LOGTAG, "Error getting linked notebook - more than one linked notebook");
                        callback.onException(new Exception("Not single linked notebook"));
                    } else {
                        final LinkedNotebook linkedNotebook = linkedNotebooks.get(0);
                        mEvernoteSession.getClientFactory().createLinkedNoteStoreClientAsync(linkedNotebook, new OnClientCallback<AsyncLinkedNoteStoreClient>() {
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
