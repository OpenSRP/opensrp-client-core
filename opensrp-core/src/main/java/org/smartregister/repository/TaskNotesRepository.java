package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Note;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 11/26/18.
 */
public class TaskNotesRepository extends BaseRepository {

    private static final String TASK_ID = "task_id";
    private static final String AUTHOR = "author";
    private static final String TIME = "time";
    private static final String TEXT = "text";

    protected static final String[] COLUMNS = new String[]{TASK_ID, AUTHOR, TIME, TEXT};


    protected static final String TASK_NOTES_TABLE = "task_note";

    private static final String CREATE_TASK_NOTE_TABLE =
            "CREATE TABLE " + TASK_NOTES_TABLE + " (" +
                    TASK_ID + " VARCHAR NOT NULL," +
                    AUTHOR + " VARCHAR , " +
                    TIME + " INTEGER NOT NULL, " +
                    TEXT + " VARCHAR  NOT NULL, " +
                    "PRIMARY KEY(" + TASK_ID + "," + TIME + "))";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TASK_NOTE_TABLE);
    }

    public void addOrUpdate(Note note, String taskId) {
        if (StringUtils.isBlank(taskId)) {
            throw new IllegalArgumentException("taskId must be specified");
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_ID, taskId);
        contentValues.put(AUTHOR, note.getAuthorString());
        contentValues.put(TIME, note.getTime().getMillis());
        contentValues.put(TEXT, note.getText());
        getWritableDatabase().replace(TASK_NOTES_TABLE, null, contentValues);
    }

    public List<Note> getNotesByTask(String taskId) {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TASK_NOTES_TABLE +
                    " WHERE " + TASK_ID + " =?", new String[]{taskId});
            while (cursor.moveToNext()) {
                notes.add(readCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return notes;
    }

    private Note readCursor(Cursor cursor) {
        Note note = new Note();
        note.setAuthorString(cursor.getString(cursor.getColumnIndex(AUTHOR)));
        note.setTime(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(TIME))));
        note.setText(cursor.getString(cursor.getColumnIndex(TEXT)));

        return note;
    }


}
