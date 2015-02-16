package com.globant.matemates.journalstudio;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Note model for the journal's internal storage.
 *
 * Created by ariel.cattaneo on 16/02/2015.
 */
public class JournalNote {
    public final static String ID = "_id";
    public final static String TITLE = "title";
    public final static String TEXT = "text";
    public final static String IMAGE = "image";

    @DatabaseField (generatedId = true, columnName = ID)
    private int mId;

    @DatabaseField (columnName = TITLE)
    private String mTitle;

    @DatabaseField (columnName = TEXT)
    private String mText;

    @DatabaseField (columnName = IMAGE, dataType = DataType.BYTE_ARRAY)
    private byte[] mImage;

    public JournalNote() {

    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public byte[] getImage() {
        return mImage;
    }

    public void setImage(byte[] image) {
        mImage = image;
    }

    @Override
    public String toString() {
        String string = "";

        string += mTitle;

        // TODO: Add part of the text

        string += (mImage != null) ? " (with image)" : "";

        return string;
    }
}
