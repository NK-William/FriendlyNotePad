package com.boitee.friendlynotes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

public class BackButtonDialog extends AppCompatDialogFragment {
    private static final String TAG = "BackButtonDialog";

    public static final String DIALOG_MESSAGE = "BackPressed_message";
    public static final String DIALOG_POSITIVE_TEXT = "BackPressed_positive_text";
    public static final String DIALOG_NEUTRAL_TEXT = "BackPressed_neutral_text";
    public static final String DIALOG_NEGATIVE_TEXT = "BackPressed_negative_text";


    interface DialogEventsOnBackPressed {
        void onPositiveDialogResult(int dialogId);
        void onNeutralDialogResult();
        // void onNegativeDialogResult(int databaseId);
    }

    private DialogEventsOnBackPressed mDialogEvents;

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: Entering onAttach, activity is " + context.toString());
        super.onAttach(context);

        // Activities containing this fragment must implement its callbacks.
        if (!(context instanceof DialogEventsOnBackPressed)) {
            throw new ClassCastException(context.toString() + " must implement DialogEvents interface");
        }
        Log.d(TAG, "onAttach: coping the context");
        mDialogEvents = (DialogEventsOnBackPressed) context;
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

        final int dialog_Id;
        String dialogMessage;
        int positiveTextId;
        int negativeTextId;
        int neutralTextId;

        if (arguments != null) {
            Log.d(TAG, "onCreateDialog: arguments != null, coooool");
            dialog_Id = arguments.getInt("DIALOG_ADD_NEW_ID");
            dialogMessage = arguments.getString(DIALOG_MESSAGE);
            positiveTextId = arguments.getInt(DIALOG_POSITIVE_TEXT);
            negativeTextId = arguments.getInt(DIALOG_NEGATIVE_TEXT);
            neutralTextId = arguments.getInt(DIALOG_NEUTRAL_TEXT);

        } else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle");
        }

        builder.setMessage(dialogMessage)
                .setPositiveButton(positiveTextId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogEvents != null) {
                            mDialogEvents.onPositiveDialogResult(dialog_Id); // remember that mDialogEvents contains the context of the activity
                            Log.d(TAG, "onClick: calling onPositiveDialogResult");
                        }
                    }
                })

                .setNeutralButton(neutralTextId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogEvents != null) {
                            mDialogEvents.onNeutralDialogResult(); // remember that mDialogEvents contains the context of the activity
                            Log.d(TAG, "onClick: calling onNeutralDialogResult");
                        }
                    }
                })
                .setNegativeButton(negativeTextId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogEvents != null) {
                            //mDialogEvents.onNegativeDialogResult();// remember that mDialogEvents contains the context of the activity
                            Log.d(TAG, "onClick: onNegativeDialogResult");
                        }
                    }
                })
        ;
        return builder.create();
    }
}
