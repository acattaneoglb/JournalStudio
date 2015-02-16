package com.globant.matemates.journalstudio;

/**
 * Note model for the journal's internal storage.
 *
 * Created by ariel.cattaneo on 16/02/2015.
 */
public class JournalNote {
    private String mTitle;
    private String mText;
    private byte[] mImage;

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
