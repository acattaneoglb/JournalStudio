package com.globant.matemates.journalstudio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.ByteArrayOutputStream;

/**
 * Note model for the journal's internal storage.
 * <p/>
 * Created by ariel.cattaneo on 16/02/2015.
 */
public class JournalNote implements Parcelable {
    public final static String ID = "_id";
    public final static String TITLE = "title";
    public final static String TEXT = "text";
    public final static String IMAGE = "image";

    @DatabaseField(generatedId = true, columnName = ID)
    private int mId;

    @DatabaseField(columnName = TITLE)
    private String mTitle;

    @DatabaseField(columnName = TEXT)
    private String mText;

    @DatabaseField(columnName = IMAGE, dataType = DataType.BYTE_ARRAY)
    private byte[] mImage;

    public JournalNote(){

    }

    public JournalNote(Parcel in) {
        this.mId = in.readInt();
        this.setTitle(in.readString());
        this.setText(in.readString());
        this.setImage((Bitmap) in.readParcelable(getClass().getClassLoader()));
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

    public Bitmap getImage() {
        try {
            return byteArrayToBitmap(mImage);
        } catch (Exception e) {
            return null;
/*
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            return Bitmap.createBitmap(100, 100, conf);
*/
        }
    }

    public void setImage(Bitmap image) {
        mImage = bitmapToByteArray(image);
    }

    @Override
    public String toString() {
        return mTitle;
    }

    private byte[] bitmapToByteArray(Bitmap bmp) {
        if (bmp != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    private Bitmap byteArrayToBitmap(byte[] array) {
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeInt(mId);
        destination.writeString(this.getTitle());
        destination.writeString(this.getText());
        destination.writeParcelable(this.getImage(), flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public JournalNote createFromParcel(Parcel in) {
            return new JournalNote(in);
        }

        public JournalNote[] newArray(int size) {
            return new JournalNote[size];
        }

    };
}
