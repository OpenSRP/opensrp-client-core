package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.Setting;
import org.smartregister.domain.SyncStatus;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SettingsRepository extends DrishtiRepository {
    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_KEY_COLUMN = "key";
    public static final String SETTINGS_VALUE_COLUMN = "value";
    public static final String SETTINGS_VERSION_COLUMN = "version";
    public static final String SETTINGS_TYPE_COLUMN = "type";
    public static final String SETTINGS_SYNC_STATUS_COLUMN = "sync_status";

    public static final String SETTINGS_SQL = "CREATE TABLE " + SETTINGS_TABLE_NAME + "(key VARCHAR PRIMARY KEY, value BLOB)";

    public static final String ADD_SETTINGS_VERSION = "ALTER TABLE " + SETTINGS_TABLE_NAME + " ADD COLUMN " + SETTINGS_VERSION_COLUMN + " TEXT;";
    public static final String ADD_SETTINGS_TYPE = "ALTER TABLE " + SETTINGS_TABLE_NAME + " ADD COLUMN " + SETTINGS_TYPE_COLUMN + " TEXT;";//Global or otherwise
    public static final String ADD_SETTINGS_SYNC_STATUS = "ALTER TABLE " + SETTINGS_TABLE_NAME + " ADD COLUMN " + SETTINGS_SYNC_STATUS_COLUMN + " TEXT;";

    private static final String[] SETTINGS_PROJECTION = {SETTINGS_KEY_COLUMN, SETTINGS_VALUE_COLUMN, SETTINGS_TYPE_COLUMN, SETTINGS_VERSION_COLUMN, SETTINGS_SYNC_STATUS_COLUMN};

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(SETTINGS_SQL);
    }

    public static void onUpgrade(SQLiteDatabase database) {
        database.execSQL(ADD_SETTINGS_VERSION);
        database.execSQL(ADD_SETTINGS_TYPE);
        database.execSQL(ADD_SETTINGS_SYNC_STATUS);
    }

    public void updateSetting(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY_COLUMN, key);
        values.put(SETTINGS_VALUE_COLUMN, value);

        replace(values);
    }

    public void updateSetting(Setting setting) {
        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY_COLUMN, setting.getKey());
        values.put(SETTINGS_VALUE_COLUMN, setting.getValue());
        values.put(SETTINGS_VERSION_COLUMN, setting.getVersion());
        values.put(SETTINGS_TYPE_COLUMN, setting.getType());
        values.put(SETTINGS_SYNC_STATUS_COLUMN, setting.getSyncStatus());

        replace(values);
    }

    public void updateBLOB(String key, byte[] value) {
        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY_COLUMN, key);
        values.put(SETTINGS_VALUE_COLUMN, value);

        replace(values);
    }

    public String querySetting(String key, String defaultValue) {
        Cursor cursor = null;
        String value = defaultValue;
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();
            cursor = database.query(SETTINGS_TABLE_NAME, new String[]{SETTINGS_VALUE_COLUMN},
                    SETTINGS_KEY_COLUMN + " = ?", new String[]{key}, null, null, null, "1");
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                value = cursor.getString(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return value;
    }

    public byte[] queryBLOB(String key) {
        byte[] value = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();
            cursor = database.query(SETTINGS_TABLE_NAME, new String[]{SETTINGS_VALUE_COLUMN},
                    SETTINGS_KEY_COLUMN + " = ?", new String[]{key}, null, null, null, "1");
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                value = cursor.getBlob(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return value;
    }

    private void replace(ContentValues values) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.replace(SETTINGS_TABLE_NAME, null, values);
    }

    public Setting querySetting(String key) {
        Cursor cursor = null;
        Setting value = null;
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();

            cursor = database.query(SETTINGS_TABLE_NAME, SETTINGS_PROJECTION
                    ,
                    SETTINGS_KEY_COLUMN + " = ?", new String[]{key}, null, null, null, "1");
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                value = queryCore(cursor);
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return value;
    }

    public List<Setting> querySettingsByType(String type) {
        Cursor cursor = null;
        List<Setting> values = new ArrayList<>();
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();

            cursor = database.query(SETTINGS_TABLE_NAME, SETTINGS_PROJECTION
                    ,
                    SETTINGS_TYPE_COLUMN + " = ?", new String[]{type}, null, null, null, null);

            while (cursor != null && cursor.moveToNext()) {
                values.add(queryCore(cursor));
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return values;
    }

    public List<Setting> queryUnsyncedSettings() {
        Cursor cursor = null;
        List<Setting> values = new ArrayList<>();
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();

            cursor = database.query(SETTINGS_TABLE_NAME, SETTINGS_PROJECTION, SETTINGS_SYNC_STATUS_COLUMN + " = ?", new String[]{SyncStatus.PENDING.name()}, null, null, null, null);

            while (cursor != null && cursor.moveToNext()) {
                values.add(queryCore(cursor));
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return values;
    }

    protected Setting queryCore(Cursor cursor) {

        Setting value = new Setting();
        value.setKey(cursor.getString(cursor.getColumnIndex(SETTINGS_KEY_COLUMN)));
        value.setValue(cursor.getString(cursor.getColumnIndex(SETTINGS_VALUE_COLUMN)));
        value.setType(cursor.getString(cursor.getColumnIndex(SETTINGS_TYPE_COLUMN)));
        value.setVersion(cursor.getString(cursor.getColumnIndex(SETTINGS_VERSION_COLUMN)));
        value.setSyncStatus(cursor.getString(cursor.getColumnIndex(SETTINGS_SYNC_STATUS_COLUMN)));

        return value;
    }

    public int queryUnsyncedSettingsCount() {
        Cursor cursor = null;
        int rowCount = 0;
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();

            cursor = database.query(SETTINGS_TABLE_NAME, new String[]{"count(*)"}, SETTINGS_SYNC_STATUS_COLUMN + " = ?", new String[]{SyncStatus.PENDING.name()}, null, null, null, null);

            if (cursor != null && cursor.moveToNext()) {
                rowCount = cursor.getInt(0);
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return rowCount;
    }
}
