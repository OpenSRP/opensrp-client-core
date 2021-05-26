package org.smartregister.repository;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.db.Column;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

import static org.apache.commons.lang3.StringUtils.repeat;


/**
 * Created by keyman on 09/03/2017.
 */
public class BaseRepository {

    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";
    public static String TYPE_Valid = "Valid";
    public static String TYPE_InValid = "Invalid";
    public static String TYPE_Task_Unprocessed = "task_unprocessed";
    public static String TYPE_Unprocessed = "unprocessed";
    public static String TYPE_Created = "Created";
    protected static final String ORDER_BY = " order by ";

    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static String COLLATE_NOCASE = " COLLATE NOCASE ";

    protected String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    public SQLiteDatabase getWritableDatabase() {
        return DrishtiApplication.getInstance().getRepository().getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return DrishtiApplication.getInstance().getRepository().getReadableDatabase();
    }

    public interface BaseTable {
        Column[] columns();

        String name();
    }

    public ArrayList<HashMap<String, String>> rawQuery(SQLiteDatabase db, String query) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    maplist.add(map);
                } while (cursor.moveToNext());
            }

            return maplist;
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

}
