package org.smartregister.util;

import android.database.Cursor;
import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import timber.log.Timber;

import static org.smartregister.AllConstants.ROWID;

public class P2PUtil {
    private static final String ID = "_id";


    public static int getMaxRowId(@NonNull String table, SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = null;
        int rowId = 0;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT max(" + ROWID + ") AS max_row_id FROM " + table, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    rowId = cursor.getInt(cursor.getColumnIndex("max_row_id"));
                }

                cursor.close();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return rowId;
    }

    public static Boolean checkIfExistsById(String table, String Id, SQLiteDatabase sqLiteDatabase) {
        Cursor mCursor = null;
        try {
            String query = "SELECT "
                    + ID
                    + " FROM "
                    + table
                    + " WHERE "
                    + ID
                    + " =?";
            mCursor = sqLiteDatabase.rawQuery(query, new String[]{Id});
            if (mCursor != null && mCursor.moveToFirst()) {

                return true;
            }
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return false;
    }
}
