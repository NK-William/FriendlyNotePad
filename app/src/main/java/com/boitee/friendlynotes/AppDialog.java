package com.boitee.friendlynotes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

public class AppDialog extends AppCompatDialogFragment {

    private static final String TAG = "AppDialog";

    public static final String database_id_key = "database_id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_TEXT = "positive_text";
    public static final String DIALOG_NEGATIVE_TEXT = "negative_text";


    interface DialogEvents {
        void onPositiveDialogResult(int databaseId);

       // void onNegativeDialogResult(int databaseId);
    }

    private DialogEvents mDialogEvents;

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: Entering onAttach, activity is " + context.toString());
        super.onAttach(context);

        // Activities containing this fragment must implement its callbacks.
        if (!(context instanceof DialogEvents)) {
            throw new ClassCastException(context.toString() + " must implement AppDialog.DialogEvents interface");
        }
        Log.d(TAG, "onAttach: coping the context");
        mDialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: Entering...");
        super.onDetach();

        // Reset the active callbacks interface, because we don't have an activity any longer.
        mDialogEvents = null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: starts");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); // AlertDialog will be able create the dialog.

        final Bundle arguments = getArguments();

        final int database_Id;
        String dialogMessage;
        int positiveTextId;
        int negativeTextId;

        if (arguments != null) {
            Log.d(TAG, "onCreateDialog: arguments != null, coooool");
            database_Id = arguments.getInt(database_id_key);
            dialogMessage = arguments.getString(DIALOG_MESSAGE);
            positiveTextId = arguments.getInt(DIALOG_POSITIVE_TEXT);
            negativeTextId = arguments.getInt(DIALOG_NEGATIVE_TEXT);

        } else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle");
        }

        builder.setMessage(dialogMessage)
                .setPositiveButton(positiveTextId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogEvents != null) {
                            mDialogEvents.onPositiveDialogResult(database_Id); // remember that mDialogEvents contains the context of the activity
                            Log.d(TAG, "onClick: calling onPositiveDialogResult");
                        }
                    }
                })
                .setNegativeButton(negativeTextId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogEvents != null) {
                            //mDialogEvents.onNegativeDialogResult(database_Id);// remember that mDialogEvents contains the context of the activity
                            Log.d(TAG, "onClick: onNegativeDialogResult");
                        }
                    }
                });
        return builder.create();
    }
}
