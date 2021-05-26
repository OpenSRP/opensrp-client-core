package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.CoreLibrary;
import org.smartregister.domain.UniqueId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 09/04/2018.
 */

public class UniqueIdRepository extends BaseRepository {
    private static final String UniqueIds_SQL = "CREATE TABLE unique_ids(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,openmrs_id VARCHAR NOT NULL,status VARCHAR NULL, used_by VARCHAR NULL,synced_by VARCHAR NULL,created_at DATETIME NULL,updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP )";
    private static final String UniqueIds_TABLE_NAME = "unique_ids";
    private static final String ID_COLUMN = "_id";
    private static final String OPENMRS_ID_COLUMN = "openmrs_id";
    private static final String STATUS_COLUMN = "status";
    private static final String USED_BY_COLUMN = "used_by";
    private static final String SYNCED_BY_COLUMN = "synced_by";
    private static final String CREATED_AT_COLUMN = "created_at";
    private static final String UPDATED_AT_COLUMN = "updated_at";
    private static final String[] UniqueIds_TABLE_COLUMNS = {ID_COLUMN, OPENMRS_ID_COLUMN, STATUS_COLUMN, USED_BY_COLUMN, SYNCED_BY_COLUMN, CREATED_AT_COLUMN, UPDATED_AT_COLUMN};

    private static final String STATUS_USED = "used";
    private static final String STATUS_NOT_USED = "not_used";
    private static final String STATUS_RESERVED = "reserved";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(UniqueIds_SQL);
    }

    public void add(UniqueId uniqueId) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            database.insert(UniqueIds_TABLE_NAME, null, createValuesFor(uniqueId));
            //database.close();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * inserts ids in bulk to the db in a transaction since normally, each time db.insert() is used, SQLite creates a transaction (and resulting journal file in the filesystem), which slows things down.
     *
     * @param ids
     */
    public void bulkInsertOpenmrsIds(List<String> ids) {

        if (ids == null || ids.isEmpty()){
            return;
        }
        SQLiteDatabase database = getWritableDatabase();

        try {
            String userName = CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();

            database.beginTransaction();
            for (String id : ids) {
                ContentValues values = new ContentValues();
                values.put(OPENMRS_ID_COLUMN, id);
                values.put(STATUS_COLUMN, STATUS_NOT_USED);
                values.put(SYNCED_BY_COLUMN, userName);
                values.put(CREATED_AT_COLUMN, dateFormat.format(new Date()));
                database.insert(UniqueIds_TABLE_NAME, null, values);
            }
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            Timber.e(e);
        } finally {
            database.endTransaction();
        }
    }

    public Long countUnUsedIds() {
        long count = 0;
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT COUNT (*) FROM " + UniqueIds_TABLE_NAME + " WHERE " + STATUS_COLUMN + "=?",
                    new String[]{STATUS_NOT_USED});
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }

        } catch (SQLException e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count;
    }

    /**
     * get next available unique id
     *
     * @return
     */
    public UniqueId getNextUniqueId() {
        UniqueId uniqueId = null;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(UniqueIds_TABLE_NAME, UniqueIds_TABLE_COLUMNS, STATUS_COLUMN + " = ?", new String[]{STATUS_NOT_USED}, null, null, CREATED_AT_COLUMN + " ASC", "1");
            List<UniqueId> ids = readAll(cursor);
            uniqueId = ids.isEmpty() ? null : ids.get(0);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (uniqueId != null) {
            reserve(uniqueId.getOpenmrsId());
        }
        return uniqueId;
    }

    /**
     * mark an openmrsid as used
     *
     * @param openmrsId
     */
    public int close(String openmrsId) {
        return reserveOrClose(openmrsId, STATUS_USED);
    }

    /**
     * reserve a uniqueId so that its not used again
     *
     * @param uniqueId
     */
    public int reserve(String uniqueId) {
        return reserveOrClose(uniqueId, STATUS_RESERVED);
    }

    /**
     * Release reserved ids so that they can be reused
     *
     * @return the number of opened ids
     */
    public int releaseReservedIds() {
        ContentValues values = new ContentValues();
        values.put(STATUS_COLUMN, STATUS_NOT_USED);
        values.put(USED_BY_COLUMN, "");
        values.put(UPDATED_AT_COLUMN, dateFormat.format(new Date()));
        return getWritableDatabase().update(UniqueIds_TABLE_NAME, values, STATUS_COLUMN + " = ?", new String[]{STATUS_RESERVED});
    }

    private int reserveOrClose(String openmrsId, String status) {
        try {
            String id;
            String userName = CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();
            if (!openmrsId.contains("-")) {
                id = formatId(openmrsId);
            } else {
                id = openmrsId;
            }
            ContentValues values = new ContentValues();
            values.put(STATUS_COLUMN, status);
            values.put(USED_BY_COLUMN, userName);
            values.put(UPDATED_AT_COLUMN, dateFormat.format(new Date()));

            return updateOpenMRSIdentifierStatus(id, values);

        } catch (Exception e) {
            Timber.e(e);
            return 0;
        }
    }

    private int updateOpenMRSIdentifierStatus(String id, ContentValues values) {

        int closed = getWritableDatabase().update(UniqueIds_TABLE_NAME, values, OPENMRS_ID_COLUMN + " = ?", new String[]{id});

        if (closed == 0 && id.contains("-")) {

            closed = getWritableDatabase().update(UniqueIds_TABLE_NAME, values, OPENMRS_ID_COLUMN + " = ?", new String[]{id.replace("-", "")});
        }

        if (closed == 0) {
            Timber.e("Error processing OpenSRP ID %s. NO SUCH ID FOUND", id);
        }

        return closed;
    }

    /**
     * mark an openmrsid as NOT used
     *
     * @param openmrsId
     */
    public int open(String openmrsId) {
        try {

            String openmrsId_ = !openmrsId.contains("-") ? formatId(openmrsId) : openmrsId;

            ContentValues values = new ContentValues();
            values.put(STATUS_COLUMN, STATUS_NOT_USED);
            values.put(USED_BY_COLUMN, "");
            values.put(UPDATED_AT_COLUMN, dateFormat.format(new Date()));
            return updateOpenMRSIdentifierStatus(openmrsId_, values);
        } catch (Exception e) {
            Timber.e(e);
            return 0;
        }
    }

    private String formatId(String openmrsId) {
        int lastIndex = openmrsId.length() - 1;
        String tail = openmrsId.substring(lastIndex);
        return openmrsId.substring(0, lastIndex) + "-" + tail;
    }

    private ContentValues createValuesFor(UniqueId uniqueId) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, uniqueId.getId());
        values.put(OPENMRS_ID_COLUMN, uniqueId.getOpenmrsId());
        values.put(STATUS_COLUMN, uniqueId.getStatus());
        values.put(USED_BY_COLUMN, uniqueId.getUsedBy());
        values.put(CREATED_AT_COLUMN, dateFormat.format(uniqueId.getCreatedAt()));
        return values;
    }

    private List<UniqueId> readAll(Cursor cursor) {
        List<UniqueId> UniqueIds = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();
            while (cursor.getCount() > 0 && !cursor.isAfterLast()) {

                UniqueIds.add(new UniqueId(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), new Date(cursor.getLong(4))));

                cursor.moveToNext();
            }
        }
        return UniqueIds;
    }


}
