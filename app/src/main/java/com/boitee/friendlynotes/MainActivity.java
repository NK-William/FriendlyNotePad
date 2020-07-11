package com.boitee.friendlynotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity implements AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";

    private SQLiteDatabase db;
    public Cursor cursor;
    public Cursor newCursor;
    private ListView notes_list_view;

    // used in onCreate(), nStop() and onRestart() to avoid crushes in onStop();
    private boolean onRestartCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        onRestartCalled = false;
        notes_list_view = findViewById(R.id.notes_list_view);

        // starting the background thread for quering data available in the database.
        Log.d(TAG, "onCreate: calling the execute method of AsyncTask");
        new QueringInBackground().execute();

        setOnItemClickListenerToListView();
        setOnItemLongClickListenerToListView();

    }


    // // keep note: ***************************************************************************************************
    @SuppressLint("StaticFieldLeak")
    private class QueringInBackground extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "queringInBackground";

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: start");

            try {
                SQLiteOpenHelper notepadDatabaseHelper = new NotepadDatabaseHelper(MainActivity.this);
                db = notepadDatabaseHelper.getReadableDatabase();
                cursor = db.query(NotesContract.TABLE_NAME,
                        new String[]{NotesContract.Columns._ID, NotesContract.Columns.NOTES_TITLE, NotesContract.Columns.NOTES_NOTES},
                        null, null, null, null, NotesContract.Columns._ID + " DESC");

                Log.d(TAG, "doInBackground: end with the cursor");
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d(TAG, "onPostExecute: start");
            if (success) {
                // using the adapter to set the values on the listView
                // keep note: ***************************************************************************************************
                CursorAdapter listAdapter = new SimpleCursorAdapter(MainActivity.this,
                        R.layout.notes_list_item,
                        cursor,
                        new String[]{NotesContract.Columns.NOTES_TITLE, NotesContract.Columns.NOTES_NOTES},
                        new int[]{R.id.notes_title, R.id.notes_body},
                        0);
                notes_list_view.setAdapter(listAdapter);


            } else {
                Toast toast = Toast.makeText(MainActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
            Log.d(TAG, "onPostExecute: end");
        }

    }

    // os pg 31 will talk too much on networking
    // pg 39 , one to many and many to one, full duplex and which one(search)
    // skipped 42
    public void setOnItemClickListenerToListView() {
        Log.d(TAG, "setOnItemClickListenerToListView: start");
        // keep note: *****************************************************************************************************
        notes_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: called ");

                    Intent addNewNotesIntent = new Intent(MainActivity.this, AddNewNotes.class);
                    addNewNotesIntent.putExtra(AddNewNotes.EXTRA_NOTES_ID, (int) id);
                    startActivity(addNewNotesIntent);

            }
        });
        Log.d(TAG, "setOnItemClickListenerToListView: end");
    }

    public void setOnItemLongClickListenerToListView(){
        notes_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int dbId = (int) id;
                deletion(dbId);

                Log.d(TAG, "onItemLongClick: returning true");
                return true;
            }
        });
    }


    void deletion(int dbId){
        Log.d(TAG, "deletion: method called to trigger the dialog");
        AppDialog appDialog = new AppDialog();
        Bundle args = new Bundle();

        args.putInt(AppDialog.database_id_key, dbId);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.dialog_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_TEXT, R.string.dialog_delete_text);
        args.putInt(AppDialog.DIALOG_NEGATIVE_TEXT, R.string.dialog_cancel_text);

        appDialog.setArguments(args);

        appDialog.show(getSupportFragmentManager(), null);
        Log.d(TAG, "deletion: method exit");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_add_new_note:
                Intent addNewNotesIntent = new Intent(this, AddNewNotes.class);
                addNewNotesIntent.putExtra(AddNewNotes.EXTRA_NOTES_ID, 0);
                startActivity(addNewNotesIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (onRestartCalled) {
            Log.d(TAG, "onStop: closing newCursor");
            newCursor.close();
        }
    }

    // using onRestart() to change the cursor
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: called");
        onRestartCalled = true;
        new QueringInBackgroundForCursorChange().execute();

    }

    @SuppressLint("StaticFieldLeak")
    private class QueringInBackgroundForCursorChange extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "queringInBackground";

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "newCursor doInBackground: start");

            try {
                SQLiteOpenHelper notepadDatabaseHelper = new NotepadDatabaseHelper(MainActivity.this);
                db = notepadDatabaseHelper.getReadableDatabase();
                newCursor = db.query(NotesContract.TABLE_NAME,
                        new String[]{NotesContract.Columns._ID, NotesContract.Columns.NOTES_TITLE, NotesContract.Columns.NOTES_NOTES},
                        null, null, null, null, NotesContract.Columns._ID + " DESC");

                Log.d(TAG, "newCursor doInBackground: end with the cursor");
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d(TAG, "newCursor onPostExecute: start");
            if (success) {
                // changing the adapter
                // keep note: ***************************************************************************************************
                CursorAdapter adapter = (CursorAdapter) notes_list_view.getAdapter();
                adapter.changeCursor(newCursor);


            } else {
                Toast toast = Toast.makeText(MainActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
            Log.d(TAG, "newCursor onPostExecute: end");
        }

    }

    @Override
    public void onPositiveDialogResult(int databaseId) {
        Log.d(TAG, "onPositiveDialogResult: deleting");
        SQLiteOpenHelper notepadDatabaseHelper = new NotepadDatabaseHelper(this);
        SQLiteDatabase db = notepadDatabaseHelper.getWritableDatabase();
        db.delete(NotesContract.TABLE_NAME, NotesContract.Columns._ID + " = ?", new String[]{Integer.toString(databaseId)});
        Log.d(TAG, "onPositiveDialogResult: done with deletion");

        //restarting the activity
        restartActivity();
        Log.d(TAG, "onPositiveDialogResult: done with activity restart");
        db.close();
    }
    // restarting the current Activity
    public void restartActivity(){
        Log.d(TAG, "restartActivity: restarting the current Activity");
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        Log.d(TAG, "restartActivity: exiting restarting method");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");

    }
}
