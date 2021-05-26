package org.smartregister.repository.mock;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

/**
 * Created by kaderchowdhury on 19/11/17.
 */

public class CursorFactoryMock implements SQLiteDatabase.CursorFactory {

    @Override
    public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {

        return new MatrixCursor(new String[]{}, 0);
    }

    public static SQLiteDatabase.CursorFactory getCursorFactory() {
        return new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
                return new MatrixCursor(new String[]{}, 0);
            }
        };
    }

}
