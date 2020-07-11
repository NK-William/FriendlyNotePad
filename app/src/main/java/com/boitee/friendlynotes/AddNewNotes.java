package com.boitee.friendlynotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewNotes extends AppCompatActivity implements BackButtonDialog.DialogEventsOnBackPressed{
    private static final String TAG = "AddNewNotes";

    private String oldTitle;
    private String oldNotes;

    public static final String EXTRA_NOTES_ID = "notes_id_number";

    private EditText titleView;
    private EditText notesView;
    private int notesId;

    private String newTitle;
    private String newNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_notes);


        titleView = findViewById(R.id.title);
        notesView = findViewById(R.id.notes);

        Intent intent = getIntent();
        notesId = intent.getIntExtra(EXTRA_NOTES_ID, -1);
        //notesId = (Integer) getIntent().getExtras().get(EXTRA_NOTES_ID); // keep note: *************************

        // if notesId != 0, then we are editing the existing notes else we are adding new notes
        if(notesId == -1){
            Toast.makeText(this, "There was an error", Toast.LENGTH_SHORT).show();
            finish();
        }else{  // the we are editing

            // in case the screen is rotated
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString("savedTitle");
            String savedNotes = savedInstanceState.getString("savedNotes");

            oldTitle = savedInstanceState.getString("oldTitle");
            oldNotes = savedInstanceState.getString("oldNotes");


            titleView.setText(savedTitle);
            notesView.setText(savedNotes);

        } else{ // extracting data from database when AddNewNotes has newly started. since (notesId !=0).
            new QueringInBackground().execute(notesId);
        }
    }
    }

    // saving the data into the database when save button is clicked (save when changes are made)
    public void save(View view) {
        Log.d(TAG, "save: button pressed");

        newTitle = titleView.getText().toString();
        newNotes = notesView.getText().toString();

        // the if block will execute only when a user is creating  new notes
        if (notesId == 0) {
            Log.d(TAG, "save: saving new notes");
            // saving without adding any data.
            if ((newTitle.isEmpty()) && (newNotes.isEmpty())) {
                Log.d(TAG, "save: no data entered");
                Toast.makeText(this, "You can't save empty notes", Toast.LENGTH_SHORT).show();

            }
            // adding only the title without adding to notes body.
            else if (!(newTitle.isEmpty()) && (newNotes.isEmpty())) {
                Log.d(TAG, "save: only title entered");
                Toast.makeText(this, "You can't save only a title, enter notes", Toast.LENGTH_SHORT).show();
            }
            // the following else block will execute only when the notes body is not empty.
            else {
                ContentValues values = new ContentValues();
                SQLiteOpenHelper notepadDatabaseHelper = new NotepadDatabaseHelper(this);
                SQLiteDatabase db = notepadDatabaseHelper.getWritableDatabase();

                values.put(NotesContract.Columns.NOTES_NOTES, newNotes);
                values.put(NotesContract.Columns.NOTES_FAVORITE, 0);

                // saving notes without saving a title
                if (newTitle.isEmpty()) {
                    Log.d(TAG, "save: saving notes without title, that's okay");
                    values.put(NotesContract.Columns.NOTES_TITLE, "");
                    db.insert(NotesContract.TABLE_NAME, null, values);
                    db.close();
                    finish();

                }
                // user here added title and notes.
                else {
                    Log.d(TAG, "save: saving notes with all fields entered");
                    values.put(NotesContract.Columns.NOTES_TITLE, newTitle);
                    db.insert(NotesContract.TABLE_NAME, null, values);
                    db.close();
                    finish();
                }
            }
        }
        // The else block will execute only when a user is editing existing notes
        else {

            //***************************************************


            Log.d(TAG, "save: saving new notes");
            // saving without adding any data.
            if ((newTitle.isEmpty()) && (newNotes.isEmpty())) {
                Log.d(TAG, "save: no data entered");
                Toast.makeText(this, "You can't save empty notes", Toast.LENGTH_SHORT).show();

            }
            // adding only the title without adding to notes body.
            else if (!(newTitle.isEmpty()) && (newNotes.isEmpty())) {
                Log.d(TAG, "save: only title entered");
                Toast.makeText(this, "You can't save only a title, enter notes", Toast.LENGTH_SHORT).show();
            }
            // the following else block will execute only when the notes body is not empty.
            else {
                if (oldTitle.equals(newTitle) && oldNotes.equals(newNotes)) {
                    Log.d(TAG, "save: not updating");
                    finish();
                } else {
                    ContentValues values = new ContentValues();
                    SQLiteOpenHelper notepadDatabaseHelper = new NotepadDatabaseHelper(this);
                    SQLiteDatabase db = notepadDatabaseHelper.getWritableDatabase();

                    // update only notes
                    if (oldTitle.equals(newTitle) && !(oldNotes.equals(newNotes))) {
                        Log.d(TAG, "save: updating only notes");
                        values.put(NotesContract.Columns.NOTES_NOTES, newNotes);
                        db.update(NotesContract.TABLE_NAME, values, NotesContract.Columns.NOTES_NOTES + " = ?", new String[]{oldNotes});
                        db.close();
                        finish();
                    }
                    // update only title
                    else if (!(oldTitle.equals(newTitle)) && oldNotes.equals(newNotes)) {
                        Log.d(TAG, "save: updating only title");
                        values.put(NotesContract.Columns.NOTES_TITLE, newTitle);
                        db.update(NotesContract.TABLE_NAME, values, NotesContract.Columns.NOTES_NOTES + " = ?", new String[]{oldNotes});
                        db.close();
                        finish();

                    }
                    // update notes and title
                    else {
                        Log.d(TAG, "save: updating all");
                        values.put(NotesContract.Columns.NOTES_NOTES, newNotes);
                        values.put(NotesContract.Columns.NOTES_TITLE, newTitle);
                        db.update(NotesContract.TABLE_NAME, values, NotesContract.Columns.NOTES_TITLE + " = ?", new String[]{oldTitle});
                        db.close();
                        finish();
                    }
                }
            }

            //***************************************************

        }

    }


    // keep note: *****************************************************************************************
    private class QueringInBackground extends AsyncTask<Integer, Void, Boolean> {
        private Cursor cursor;
        private SQLiteDatabase db;

        @Override
        protected Boolean doInBackground(Integer... notesId) {
            int notesIdValue = notesId[0];
            try {
                SQLiteOpenHelper notepadDatabaseHelper = new NotepadDatabaseHelper(AddNewNotes.this);
                db = notepadDatabaseHelper.getWritableDatabase();
                cursor = db.query(NotesContract.TABLE_NAME,
                        new String[]{NotesContract.Columns.NOTES_TITLE, NotesContract.Columns.NOTES_NOTES},
                        NotesContract.Columns._ID + " = ?",
                        new String[]{Integer.toString(notesIdValue)},
                        null, null, null);

                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // keep note: *****************************************************************************************
            if (success) {
                // if success is true then set the values to the editTexts.
                if (cursor.moveToFirst()) {
                    String title = cursor.getString(0);
                    oldTitle = title; // this will be used when saving(to check whether there are changes)
                    String notes = cursor.getString(1);
                    oldNotes = notes; // this will be used when saving(to check whether there are changes)


                    titleView.setText(title);

                    notesView.setText(notes);

                    cursor.close();
                    db.close();
                }

            } else {
                Toast toast = Toast.makeText(AddNewNotes.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();

            }
        }
    }

    @Override
    public void onBackPressed() {

        newTitle = titleView.getText().toString();
        newNotes = notesView.getText().toString();


        // the if block will execute only when a user is creating  new notes
        if (notesId == 0) {

            // exiting without adding any data.
            if ((newTitle.isEmpty()) && (newNotes.isEmpty())) {
                Log.d(TAG, "onBackPressed: exiting on empty title and notes(no dialog)");
                Log.d(TAG, "save: no data entered");
                super.onBackPressed();

            }
            // adding only the title without adding to notes body.
            else if (!(newTitle.isEmpty()) && (newNotes.isEmpty())) {
                Log.d(TAG, "onBackPressed: only title entered(no dialog)");
                Toast.makeText(this, "You can't save notes with title only", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
            // the following else block will execute only when the notes body is not empty.
            else {

                initialisingDialogOnAddNew();

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@1

            }
        }
        // The else block will execute only when a user is editing existing notes
        else {

            // there are not changes
            if (oldTitle.equals(newTitle) && oldNotes.equals(newNotes)) {
                Log.d(TAG, "onBackPressed: no changes(no dialog)");
                super.onBackPressed();
            } else {

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2

                initialisingDialogOnUpdate();
            }

        }

    }

    private void initialisingDialogOnAddNew(){
        Log.d(TAG, "initialisingDialogOnAddNew: method called to trigger the dialog");
        BackButtonDialog backButtonDialog = new BackButtonDialog();
        Bundle args = new Bundle();

        args.putInt("DIALOG_ADD_NEW_ID", 1);
        args.putString(BackButtonDialog.DIALOG_MESSAGE, getString(R.string.dialogBackButtonOnAddNewMessage));
        args.putInt(BackButtonDialog.DIALOG_POSITIVE_TEXT, R.string.dialogBackButtonOnAddNewSaveText);
        args.putInt(BackButtonDialog.DIALOG_NEGATIVE_TEXT, R.string.dialogBackButtonOnAddNewCancelText);
        args.putInt(BackButtonDialog.DIALOG_NEUTRAL_TEXT, R.string.dialogBackButtonOnAddNewDiscardText);

        backButtonDialog.setArguments(args);

        backButtonDialog.show(getSupportFragmentManager(), null);
        Log.d(TAG, "initialisingDialogOnAddNew: method exit");

    }
    private void initialisingDialogOnUpdate(){
        Log.d(TAG, "initialisingDialogOnUpdate: method called to trigger the dialog");
        BackButtonDialog backButtonDialog = new BackButtonDialog();
        Bundle args = new Bundle();

        args.putInt("DIALOG_ADD_NEW_ID", 2);
        args.putString(BackButtonDialog.DIALOG_MESSAGE, getString(R.string.dialogBackButtonOnUpdateMessage));
        args.putInt(BackButtonDialog.DIALOG_POSITIVE_TEXT, R.string.dialogBackButtonOnUpdateSaveText);
        args.putInt(BackButtonDialog.DIALOG_NEGATIVE_TEXT, R.string.dialogBackButtonOnUpdateCancelText);
        args.putInt(BackButtonDialog.DIALOG_NEUTRAL_TEXT, R.string.dialogBackButtonOnUpdateDiscardText);

        backButtonDialog.setArguments(args);

        backButtonDialog.show(getSupportFragmentManager(), null);
        Log.d(TAG, "initialisingDialogOnAddNew: method exit");

    }

    @Override
    public void onPositiveDialogResult(int dialog_id) {
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@1

        ContentValues values = new ContentValues();
        SQLiteOpenHelper notepadDatabaseHelper = new NotepadDatabaseHelper(this);
        SQLiteDatabase db = notepadDatabaseHelper.getWritableDatabase();

        switch (dialog_id){
            case 1:

                values.put(NotesContract.Columns.NOTES_NOTES, newNotes);
                values.put(NotesContract.Columns.NOTES_FAVORITE, 0);

                // saving notes without saving a title
                if (newTitle.isEmpty()) {
                    Log.d(TAG, "save: saving notes without title, that's okay");
                    values.put(NotesContract.Columns.NOTES_TITLE, "");
                    db.insert(NotesContract.TABLE_NAME, null, values);
                    db.close();
                    finish();

                }
                // user here added title and notes.
                else {
                    Log.d(TAG, "save: saving notes with all fields entered");
                    values.put(NotesContract.Columns.NOTES_TITLE, newTitle);
                    db.insert(NotesContract.TABLE_NAME, null, values);
                    db.close();
                    finish();
                }

                break;
            case 2:

                // ******************************************************************************

                Log.d(TAG, "save: saving new notes");
                // saving without adding any data.
                if ((newTitle.isEmpty()) && (newNotes.isEmpty())) {
                    Log.d(TAG, "save: no data entered");
                    Toast.makeText(this, "You can't save empty notes", Toast.LENGTH_SHORT).show();

                }
                // adding only the title without adding to notes body.
                else if (!(newTitle.isEmpty()) && (newNotes.isEmpty())) {
                    Log.d(TAG, "save: only title entered");
                    Toast.makeText(this, "You can't save only a title, enter notes", Toast.LENGTH_SHORT).show();
                }
                // the following else block will execute only when the notes body is not empty.
                else {

                    // update only notes
                    if (oldTitle.equals(newTitle) && !(oldNotes.equals(newNotes))) {
                        Log.d(TAG, "onBackPressed: updating only notes");
                        values.put(NotesContract.Columns.NOTES_NOTES, newNotes);
                        db.update(NotesContract.TABLE_NAME, values, NotesContract.Columns.NOTES_NOTES + " = ?", new String[]{oldNotes});
                        db.close();
                        finish();
                    }
                    // update only title
                    else if (!(oldTitle.equals(newTitle)) && oldNotes.equals(newNotes)) {
                        Log.d(TAG, "onBackPressed: updating only title");
                        values.put(NotesContract.Columns.NOTES_TITLE, newTitle);
                        db.update(NotesContract.TABLE_NAME, values, NotesContract.Columns.NOTES_NOTES + " = ?", new String[]{oldNotes});
                        db.close();
                        finish();

                    }
                    // update notes and title
                    else {
                        Log.d(TAG, "onBackPressed: updating all");
                        values.put(NotesContract.Columns.NOTES_NOTES, newNotes);
                        values.put(NotesContract.Columns.NOTES_TITLE, newTitle);
                        db.update(NotesContract.TABLE_NAME, values, NotesContract.Columns.NOTES_NOTES + " = ?", new String[]{oldNotes});
                        db.close();
                        finish();
                    }
                    break;

                }


                //********************************************************************************

        }

    }

    @Override
    public void onNeutralDialogResult() {
        Log.d(TAG, "onNeutralDialogResult: just finish");
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("savedTitle", titleView.getText().toString());
        outState.putString("savedNotes", notesView.getText().toString());

        outState.putString("oldTitle", oldTitle);
        outState.putString("oldNotes", oldNotes);

        super.onSaveInstanceState(outState);
    }
}
