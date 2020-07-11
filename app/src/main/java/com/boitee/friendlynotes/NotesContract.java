package com.boitee.friendlynotes;

import android.content.ContentUris;
import android.net.Uri;

public class NotesContract {

    static final String TABLE_NAME = "Notes";

    // Notes Fields
    public static class Columns {
        public static final String _ID = "_id";
        public static final String NOTES_TITLE = "Title";
        public static final String NOTES_NOTES = "Notes";
        public static final String NOTES_FAVORITE = "Favourite";

        private Columns() {
            // private constructor to prevent instantiation
        }


    }

    /**
     * The URI to access the Tasks table
     */
//    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME); // now CONTENT_URI can be used by external classes including external
//    // apps that uses our content provider to refer to Tasks table
//
//    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
//    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
//
//    static Uri buildTaskUri(long taskId) {
//        return ContentUris.withAppendedId(CONTENT_URI, taskId);
//    }
//
//    static long getTaskId(Uri uri) {
//        return ContentUris.parseId(uri);
//    }
}
