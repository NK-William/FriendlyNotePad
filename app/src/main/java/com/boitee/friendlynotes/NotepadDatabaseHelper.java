package com.boitee.friendlynotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotepadDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "NotepadDatabaseHelper";

    private static final String DB_NAME = "Notepad";
    private static final int DB_VERSION = 1;

    // implement AppDatabase as singleton
    //private static NotepadDatabaseHelper instance = null;

    NotepadDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

//    static NotepadDatabaseHelper getInstance(Context context){
//        if(instance == null){        // the function insures that we only have one copy of the instance
//            Log.d(TAG, "getInstance: create a new instance");
//            instance = new NotepadDatabaseHelper(context);
//        }
//        return instance;
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: start");
        db.execSQL("CREATE TABLE " + NotesContract.TABLE_NAME + " ("
                + NotesContract.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NotesContract.Columns.NOTES_TITLE + " TEXT, "
                + NotesContract.Columns.NOTES_NOTES + " TEXT NOT NULL, "
                + NotesContract.Columns.NOTES_FAVORITE + " NUMERIC);");

        Log.d(TAG, "onCreate: end");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
