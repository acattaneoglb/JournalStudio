package com.globant.matemates.journalstudio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Helper class to manage database creation.
 *
 * Created by ariel.cattaneo on 05/02/2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private final static String LOG_TAG = DatabaseHelper.class.getSimpleName();

    private final static String DATABASE_NAME = "notes.db";
    private final static int DATABASE_VERSION = 1;

    private Dao<JournalNote, Integer> mNoteDao = null;

    public Dao<JournalNote, Integer> getNoteDao() throws SQLException {
        if (mNoteDao == null) {
            mNoteDao = getDao(JournalNote.class);
        }
        return mNoteDao;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.d(LOG_TAG, "Creating database");
            TableUtils.createTable(connectionSource, JournalNote.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error creating database.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
